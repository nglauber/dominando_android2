package dominando.android.contatos;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.OperationApplicationException;
import android.graphics.Bitmap;
import android.os.RemoteException;
import android.provider.ContactsContract;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class ContatoUtils {
    public static void inserirContato(Context context, String nome, String telefone,
                                      String endereco, Bitmap foto) {
        // Lista de operações que serão realizadas em batch
        ArrayList<ContentProviderOperation> operation =
                new ArrayList<ContentProviderOperation>();
        // Armazenará o id interno do contato
        // e servirá para inserir os detalhes
        int backRefIndex = 0;
        // Associa o contato à conta-padrão do telefone
        operation.add(
                ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI).
                        withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null).
                        withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null).build());
        // Adiciona o nome do contato e alimenta id
        operation.add(
                ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI).
                        withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, backRefIndex).
                        withValue(ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE).
                        withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                                nome).build());
        // Adiciona um endereço ao contato a partir do id
        operation.add(
                ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI).
                        withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, backRefIndex).
                        withValue(ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE).
                        withValue(ContactsContract.CommonDataKinds.StructuredPostal.
                                FORMATTED_ADDRESS, endereco).build());
        // Associa um telefone ao contato do tipo "Home"
        operation.add(
                ContentProviderOperation.newInsert(
                        ContactsContract.Data.CONTENT_URI).
                        withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, backRefIndex).
                        withValue(ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE).
                        withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, telefone).
                        withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                                ContactsContract.CommonDataKinds.Phone.TYPE_HOME).
                        build());
        // Adiciona imagem ao contato
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        foto.compress(Bitmap.CompressFormat.PNG , 75, stream);
        operation.add(
                ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, backRefIndex)
                        .withValue(ContactsContract.Data.IS_SUPER_PRIMARY, 1)
                        .withValue(ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Photo.PHOTO, stream.toByteArray())
                        .build());
        // Aplica o batch de inclusão
        try {
            context.getContentResolver().applyBatch(
                    ContactsContract.AUTHORITY, operation);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }
    }
}

