package zdoctor.zskilltree.api.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
public @interface ClassNameMapper {
    String mapping();
}
