package dominando.android.enghaw;

import android.test.suitebuilder.annotation.SmallTest;
import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.Matchers.notNullValue;

@SmallTest
public class DiscoHttpTest {
    @Test
    public void testDownloadLivros() {
        Disco[] discos = DiscoHttp.obterDiscosDoServidor();
        assertThat(discos, notNullValue());
        assertThat(discos.length, is(not(lessThanOrEqualTo(0))));
    }
}
