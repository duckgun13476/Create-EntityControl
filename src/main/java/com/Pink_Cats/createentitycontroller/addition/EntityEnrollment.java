package com.Pink_Cats.createentitycontroller.addition;

public class EntityEnrollment {

        // 私有变量
        private static int ControlStatus = 0;

        // Getter 方法
        public static int getControlStatus() {
            return ControlStatus;
        }

        // Setter 方法
        public static void setControlStatus(int ControlStatus) {
            EntityEnrollment.ControlStatus = ControlStatus;
        }


}
