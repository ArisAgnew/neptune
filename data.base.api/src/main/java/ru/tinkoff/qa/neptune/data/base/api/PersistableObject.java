package ru.tinkoff.qa.neptune.data.base.api;

import org.datanucleus.enhancement.Persistable;
import org.datanucleus.identity.ObjectId;
import org.datanucleus.state.ObjectProvider;
import ru.tinkoff.qa.neptune.core.api.steps.LoggableObject;
import ru.tinkoff.qa.neptune.data.base.api.captors.IsQueryCaptured;
import ru.tinkoff.qa.neptune.data.base.api.connection.data.DBConnection;

import javax.jdo.annotations.NotPersistent;
import java.util.Objects;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;
import static javax.jdo.JDOHelper.isPersistent;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static ru.tinkoff.qa.neptune.data.base.api.connection.data.DBConnectionStore.getKnownConnection;

/**
 * This abstract class is designed to mark persistable classes.
 */
public abstract class PersistableObject extends OrmObject implements Cloneable, LoggableObject, GotByQuery {

    @NotPersistent
    private transient Object query;

    @Override
    public String toString() {
        var name = fromTable();
        if (!isPersistent(this)) {
            return format("Not stored data base element mapped by %s", name);
        }

        return ofNullable(getIdValue())
                .map(o -> format("Stored item Id=[%s] table [%s]", o, name))
                .orElseGet(() -> format("Stored item without id table [%s]", name));
    }

    /**
     * Returns object id
     *
     * @return the value of id.
     */
    public Object getIdValue() {
        if (!isPersistent(this)) {
            return null;
        }

        return ofNullable(((Persistable) this).dnGetObjectId())
                .map(o -> {
                    if (ObjectId.class.isAssignableFrom(o.getClass())) {
                        return ((ObjectId) o).getKey();
                    }

                    return o;
                }).orElse(null);
    }

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        var otherClass = obj.getClass();
        if (!PersistableObject.class.isAssignableFrom(otherClass)) {
            return false;
        }

        var thisClass  = this.getClass();

        if (!thisClass.equals(otherClass)) {
            return false;
        }

        if (!isPersistent(this) && !isPersistent(obj)) {
            return super.equals(obj);
        }

        if ((!isPersistent(this) && isPersistent(obj)) ||
                (isPersistent(this) && !isPersistent(obj))) {
            return false;
        }

        if (((Persistable) this).dnGetObjectId() == null && ((Persistable) obj).dnGetObjectId() == null) {
            return super.equals(obj);
        }

        return Objects.equals(((Persistable) this).dnGetObjectId(), ((Persistable) obj).dnGetObjectId());
    }

    public Object getQuery() {
        return query;
    }

    /**
     * Returns table name
     *
     * @return name of the table or name of the class when this object is not received from the data store
     */
    public String fromTable() {
        String tableName = this.getClass().getName();
        if (!isPersistent(this)) {
            return tableName;
        }

        var persistable = (Persistable) this;
        var stateManager = persistable.dnGetStateManager();

        return ofNullable(stateManager).map(sm -> {
            if (ObjectProvider.class.isAssignableFrom(sm.getClass())) {
                return ofNullable(((ObjectProvider) sm).getClassMetaData())
                        .map(abstractClassMetaData -> {
                            var table = abstractClassMetaData.getTable();
                            if (!isBlank(table)) {
                                return table;
                            }
                            return tableName;
                        })
                        .orElse(tableName);
            }
            return tableName;
        }).orElse(tableName);
    }

    public PersistableObject setQuery(Object query) {
        this.query = ofNullable(query).map(QueryInfo::new).orElse(null);
        return this;
    }

    private static class QueryInfo implements IsQueryCaptured, GotByQuery {
        private final Object query;
        private boolean isCaptured;

        private QueryInfo(Object query) {
            this.query = query;
        }

        @Override
        public Object getQuery() {
            return query;
        }

        @Override
        public boolean isCaptured() {
            return isCaptured;
        }

        @Override
        public void setCaptured() {
            isCaptured = true;
        }

        @Override
        public String toString() {
            return query.toString();
        }
    }

    static DBConnection getConnection(Class<? extends PersistableObject> clazz) {
        return ofNullable(clazz.getAnnotation(ConnectionToUse.class))
                .map(connectionToUse -> getKnownConnection(connectionToUse.connectionSupplier(), true))
                .orElseGet(() -> {
                    var pack = clazz.getPackage();
                    var connectionToUse = pack.getAnnotation(ConnectionToUse.class);

                    if (connectionToUse != null) {
                        return getKnownConnection(connectionToUse.connectionSupplier(), true);
                    }

                    for (Package p: clazz.getClassLoader().getDefinedPackages()) {
                        connectionToUse = p.getAnnotation(ConnectionToUse.class);
                        if (connectionToUse != null) {
                            return getKnownConnection(connectionToUse.connectionSupplier(), true);
                        }
                    }

                    throw new IllegalArgumentException(format("No annotation %s is defined for class %s/its packages",
                            ConnectionToUse.class,
                            clazz.getName()));
                });
    }

    DBConnection getConnection() {
        return getConnection(this.getClass());
    }
}
