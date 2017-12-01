/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyecto.prototicket.Utils;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Base64;

import java.io.*;

import proyecto.prototicket.schemas.Ticket.TicketDb;

/**
 *
 * @author User
 */
public class ClassCompression {
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String encode(TicketDb ticket) throws IOException {
        // Serializacion
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(ticket.getUuid());
        oos.writeObject(ticket.getRuta());
        oos.writeObject(ticket.getValor());
        oos.writeObject(ticket.getFecha_nicial());
        oos.writeObject(ticket.getPunto_venta());
        oos.writeObject(ticket.getHora_llegada());
        oos.writeObject(ticket.getFechaViaje());
        oos.writeObject(ticket.getHora_salida());
        oos.writeObject(ticket.getDS());
        oos.close();
        return Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP);
    }

    public static TicketDb decode(String encoded) throws IOException, ClassNotFoundException {
        byte[] decoded = Base64.decode(encoded, Base64.NO_WRAP);
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(decoded));
        //TicketDb(String uuid, String ruta, String valor, String fecha_nicial, String punto_venta, String hora_llegada, String fechaViaje, String hora_salida, String DS)
        TicketDb obj = new TicketDb((String)ois.readObject(),
                (String)ois.readObject(),
                (String)ois.readObject(),
                (String)ois.readObject(),
                (String)ois.readObject(),
                (String)ois.readObject(),
                (String)ois.readObject(),
                (String)ois.readObject(),
                (String)ois.readObject()
        );

        ois.close();
        return obj;

    }

}
