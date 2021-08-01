package serialize;

import extension.SPI;

/** 序列化接口
 *
 * 用于规定序列化的规范
 */

@SPI
public interface Serializer {

    /**
     * 序列化
     *
     * @param obj 需要序列化的对象
     * @return 字节数组
     */
    byte[] serialize(Object obj);

    /**
     * 反序列化
     *
     * @param bytes 序列化的字节数组
     * @param clazz 反序列化的目标类
     * @param <T> 类的类型，{@code String.class}的类是{@code Class<String>},对不清楚的类型{@code Class<?>}
     *
     * @return 反序列化后的类对象
     */
    <T> T deserialize(byte[] bytes,Class<T> clazz);
}
