package com.xwintop.xcore;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private long id;

    private String name;

    private double length;

    private Date lastLogin;

    public static List<User> createUsers() {
        return Arrays.asList(
            new User(1, "张三", 177, new Date()),
            new User(2, "李四", 172, new Date()),
            new User(3, "王五", 168, new Date()),
            new User(4, "赵六", 165, new Date())
        );
    }
}
