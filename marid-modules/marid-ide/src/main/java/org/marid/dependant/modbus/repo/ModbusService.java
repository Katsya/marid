/*
 * Copyright (c) 2016 Dmitry Ovchinnikov
 * Marid, the free data acquisition and visualization software
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.marid.dependant.modbus.repo;

import com.digitalpetri.modbus.ExceptionCode;
import com.digitalpetri.modbus.FunctionCode;
import com.digitalpetri.modbus.requests.ReadHoldingRegistersRequest;
import com.digitalpetri.modbus.requests.ReadInputRegistersRequest;
import com.digitalpetri.modbus.responses.ReadHoldingRegistersResponse;
import com.digitalpetri.modbus.responses.ReadInputRegistersResponse;
import com.digitalpetri.modbus.slave.ModbusTcpSlave;
import com.digitalpetri.modbus.slave.ModbusTcpSlaveConfig;
import com.digitalpetri.modbus.slave.ServiceRequestHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.HashedWheelTimer;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.apache.commons.lang3.ArrayUtils;
import org.marid.dependant.modbus.ModbusPane;
import org.marid.dependant.modbus.devices.AbstractDevice;
import org.marid.jfx.action.MaridActions;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;

import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.logging.Level.WARNING;
import static org.marid.logging.Log.log;

/**
 * @author Dmitry Ovchinnikov.
 * @since 0.9
 */
@Service
public class ModbusService implements ServiceRequestHandler {

    private final ModbusPane pane;
    private final ModbusConfig config;
    private final BooleanProperty active = new SimpleBooleanProperty();
    private final ExecutorService executor = new ThreadPoolExecutor(
            0, 2, 1L, SECONDS, new SynchronousQueue<>(), new CallerRunsPolicy());
    private final HashedWheelTimer timer = new HashedWheelTimer();
    private final EventLoopGroup eventLoop = new NioEventLoopGroup();

    private ModbusTcpSlave slave;

    public ModbusService(ModbusPane pane, ModbusConfig config) {
        this.pane = pane;
        this.config = config;
    }

    public void start() {
        try {
            slave = new ModbusTcpSlave(new ModbusTcpSlaveConfig.Builder()
                    .setExecutor(executor)
                    .setWheelTimer(timer)
                    .setEventLoop(eventLoop)
                    .build());
            slave.bind(config.host.get(), config.port.get());
            slave.setRequestHandler(this);
            active.set(true);
        } catch (Exception x) {
            log(WARNING, "Unable to listen {0}:{1}", x, config.host.get(), config.port.get());
            stop();
        }
    }

    public void stop() {
        if (slave != null) {
            try {
                slave.shutdown();
            } catch (Exception x) {
                log(WARNING, "Unable to close {0}:{1}", x, config.host.get(), config.port.get());
            } finally {
                slave = null;
                active.set(false);
            }
        }
    }

    @PreDestroy
    private void destroy() {
        stop();
        eventLoop.shutdownGracefully();
        timer.stop();
        executor.shutdown();
    }

    public BooleanProperty activeProperty() {
        return active;
    }

    private byte[] data(int address, FunctionCode function) {
        return MaridActions.execute(() -> pane.getChildren().stream()
                .filter(AbstractDevice.class::isInstance)
                .map(AbstractDevice.class::cast)
                .filter(d -> d.getFunctionCode() == function)
                .filter(d -> d.getAddress() == address)
                .map(AbstractDevice::getData)
                .findFirst()
                .orElse(ArrayUtils.EMPTY_BYTE_ARRAY)
        );
    }

    @Override
    public void onReadHoldingRegisters(ServiceRequest<ReadHoldingRegistersRequest, ReadHoldingRegistersResponse> s) {
        final byte[] data = data(s.getRequest().getAddress(), s.getRequest().getFunctionCode());
        if (data == ArrayUtils.EMPTY_BYTE_ARRAY) {
            final ByteBuf buf = ByteBufAllocator.DEFAULT.heapBuffer(data.length);
            buf.setBytes(0, data);
            s.sendResponse(new ReadHoldingRegistersResponse(buf));
        } else {
            s.sendException(ExceptionCode.IllegalDataAddress);
        }
    }

    @Override
    public void onReadInputRegisters(ServiceRequest<ReadInputRegistersRequest, ReadInputRegistersResponse> s) {
        final byte[] data = data(s.getRequest().getAddress(), s.getRequest().getFunctionCode());
        if (data == ArrayUtils.EMPTY_BYTE_ARRAY) {
            final ByteBuf buf = ByteBufAllocator.DEFAULT.heapBuffer(data.length);
            buf.setBytes(0, data);
            s.sendResponse(new ReadInputRegistersResponse(buf));
        } else {
            s.sendException(ExceptionCode.IllegalDataAddress);
        }
    }
}