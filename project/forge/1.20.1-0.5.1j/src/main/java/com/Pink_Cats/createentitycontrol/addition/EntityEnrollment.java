package com.Pink_Cats.createentitycontrol.addition;

public class EntityEnrollment {

        private static int ControlStatus = 0;

        public static int getControlStatus() {
            return ControlStatus;
        }

        public static void setControlStatus(int ControlStatus) {
            EntityEnrollment.ControlStatus = ControlStatus;
        }


}
