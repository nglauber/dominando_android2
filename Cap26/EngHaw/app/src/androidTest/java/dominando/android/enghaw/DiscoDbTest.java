package dominando.android.enghaw;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.RenamingDelegatingContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(AndroidJUnit4.class)
public class DiscoDbTest {
    Context mMockContext;
    @Before
    public void setup(){
        mMockContext = new RenamingDelegatingContext(
                InstrumentationRegistry.getInstrumentation().getTargetContext(), "test_");
        mMockContext.deleteDatabase(DiscoDbHelper.DB_NAME);
    }
    @Test
    public void testInserirLivro() {
        Disco disco = new Disco();
        disco.titulo = "Disco Teste";
        disco.ano = 2015;
        disco.gravadora = "BMG";
        disco.formacao = new String[]{"Eu", "tu"};
        disco.faixas = new String[]{"Faixa1", "Faixa2"};
        DiscoDb discoDb = new DiscoDb(mMockContext);
        discoDb.inserir(disco);
        assertThat(discoDb.favorito(disco), is(true));
        discoDb.excluir(disco);
        assertThat(discoDb.favorito(disco), is(false));
    }
}



