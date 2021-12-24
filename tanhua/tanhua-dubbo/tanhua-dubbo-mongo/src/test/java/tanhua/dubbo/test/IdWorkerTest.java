package tanhua.dubbo.test;

import com.tanhua.dubbo.DubboMongoApplication;
import com.tanhua.dubbo.utils.IdWorker;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DubboMongoApplication.class)
public class IdWorkerTest {

    @Autowired
    private IdWorker idWorker;

    @Test
    public void test() {
        System.out.println(idWorker.getNextId("test"));
        System.out.println(idWorker.getNextId("test"));
        System.out.println(idWorker.getNextId("test"));
        System.out.println(idWorker.getNextId("test"));
        System.out.println(idWorker.getNextId("test"));
        System.out.println(idWorker.getNextId("test"));
        System.out.println(idWorker.getNextId("test"));
    }
}
