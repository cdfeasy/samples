package cdf.sample.test;

import cdf.sample.RabbitSample;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by d.asadullin on 22.01.2015.
 */
public class RabbitTest {

    @Test
    public void Test() throws IOException {
        RabbitSample sample=new RabbitSample();
        sample.connect();
        sample.send();
        sample.receive();
    }

}
