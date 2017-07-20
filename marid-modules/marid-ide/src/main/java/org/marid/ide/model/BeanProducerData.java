package org.marid.ide.model;

import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.marid.runtime.beans.BeanMember;
import org.marid.runtime.beans.BeanProducer;

import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * @author Dmitry Ovchinnikov
 */
public class BeanProducerData {

    public final StringProperty signature = new SimpleStringProperty();
    public final ObservableList<BeanMemberData> args = FXCollections.observableArrayList(BeanMemberData::observables);

    public BeanProducerData(BeanProducer producer) {
        signature.set(producer.signature);
        args.setAll(Stream.of(producer.args).map(BeanMemberData::new).collect(toList()));
    }

    public BeanProducerData() {
    }

    public String getSignature() {
        return signature.get();
    }

    public Observable[] observables() {
        return new Observable[] {signature, args};
    }

    public BeanProducer toProducer() {
        return new BeanProducer(signature.get(), args.stream().map(BeanMemberData::toMember).toArray(BeanMember[]::new));
    }
}
