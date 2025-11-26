package kr.modusplant.shared.util;

import java.util.List;

public abstract class CastUtils {
    public static List<String> downcastToStringList(Object object) {
        if (object instanceof List<?> list) {
            list.forEach(
                    element ->
                    {
                        if (!(element instanceof String)) {
                            throw new ClassCastException("String으로의 다운캐스팅에 실패하였습니다. ");
                        }
                    });
            //noinspection unchecked
            return (List<String>) list;
        } else {
            throw new ClassCastException("List<?>로의 다운캐스팅에 실패하였습니다. ");
        }
    }
}
