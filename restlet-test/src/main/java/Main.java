import org.restlet.Server;
import org.restlet.data.Protocol;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

public class Main extends ServerResource {

    public static void main(String[] args) throws Exception {
        new Server(Protocol.HTTP, 8182, Main.class).start();
    }

//    @Post
//    public String toString() {
//        return "xzc";
//    }

    @Post
    public String createVmApi(String request) {
        return request;
    }
}