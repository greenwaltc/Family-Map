package Model;

import java.lang.reflect.Field;

public class HttpPort {

    private String mServerHost;
    private String mServerPort;

    public HttpPort() {}

    public HttpPort(String serverHost, String serverPort) {
        mServerHost = serverHost;
        mServerPort = serverPort;
    }

    public String getServerHost() {
        return mServerHost;
    }

    public void setServerHost(String serverHost) {
        mServerHost = serverHost;
    }

    public String getServerPort() {
        return mServerPort;
    }

    public void setServerPort(String serverPort) {
        mServerPort = serverPort;
    }

    public boolean anyNull() {
        Field[] var1 = this.getClass().getDeclaredFields();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            Field f = var1[var3];

            try {
                if (f.get(this) == null) {
                    return true;
                }
            } catch (IllegalAccessException var6) {
                var6.printStackTrace();
            }
        }

        return false;
    }
}
