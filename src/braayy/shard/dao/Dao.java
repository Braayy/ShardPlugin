package braayy.shard.dao;

public interface Dao<K, T> {

    void insert(T object);

    void update(T object);

    void delete(T object);

    T select(K key);

}
