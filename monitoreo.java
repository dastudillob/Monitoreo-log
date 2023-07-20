/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

import Clases.Conectar;
import Clases.ConexionSFTP;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TimerTask;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Daniel
 */
public class monitoreo extends javax.swing.JFrame {

    /**
     * Creates new form monitoreo
     */
    
    //conectar a oracle y stfp
    static Statement st;
    Conectar cc = new Conectar();
    Connection cn = cc.conexion();
        private static final String USER = "cargauser";
    private static final String HOST = "164.96.15.59";
    private static final int PUERTO = 22;
    private static final String PASS = "carga.2018";
     

    public monitoreo() throws IOException, SQLException {
        initComponents();
        this.setLocationRelativeTo(null);

        mostrarEntradaPISEE("C:\\Users\\Daniel\\Desktop\\dp_65.34.log");
        mostrarEntradaNoPisee("C:\\Users\\Daniel\\Desktop\\dp_65.34.log");
        separarContenido("C:\\Users\\Daniel\\Desktop\\dp_65.34.log");
        /*ListarSalida("C:\\Users\\Daniel\\Desktop\\dp_65.34.log");
        /*EncontrarCampos();*/

    }

    //lee todos los registros que sean EntradaPISEE y obtiene un group by, para luego mostrar resultado en la tabla 1
    void mostrarEntradaPISEE(String archivo) throws FileNotFoundException, IOException {
        String[] cadena = new String[2];
        String[] llenarTabla = new String[2];
        String[] resu = new String[1];
        ArrayList<String> ArrEP = new ArrayList<>();
        int cont = 0;
        DefaultTableModel tabla = new DefaultTableModel();
        tabla.addColumn("                INSTITUCIÓN");
        tabla.addColumn("                CANTIDAD");
        tb_monitoreo.setModel(tabla);
        String texto = "EntradaPISEE";

        FileReader f = new FileReader(archivo);
        BufferedReader b = new BufferedReader(f);
        
        ArrayList<String> lineas = new ArrayList<String>();
        while ((cadena[0] = b.readLine()) != null) {
            int intIndex = cadena[0].indexOf(texto);
            if (intIndex != -1) {
                int Indice1 = cadena[0].lastIndexOf("/");
                int Indice2 = cadena[0].indexOf("cgi-bin/");
                String inst = cadena[0].substring(Indice2 + 8, Indice1);
                lineas.add(inst);
                cont = cont + 1;
            }
        }
        
        Map<String, Long> result
                = lineas.stream().collect(
                        Collectors.groupingBy(
                                Function.identity(), Collectors.counting()
                        )
                );
        StringTokenizer TokenEP = new StringTokenizer(result.toString(), ",");
        while (TokenEP.hasMoreTokens()) {
            ArrEP.add(TokenEP.nextToken());
        }
        for (int x = 0; x < ArrEP.size()-1; x++) {
            int IndENP = ArrEP.get(x).indexOf("=");
            String nombreENP = ArrEP.get(x).substring(1, IndENP);
            String cantENP = ArrEP.get(x).substring(IndENP+1,ArrEP.get(x).length());
            llenarTabla[0] = nombreENP;
            llenarTabla[1] = cantENP;
            tabla.addRow(llenarTabla);            
        }
        String UltimoToken = ArrEP.get(ArrEP.size()-1).substring(1,ArrEP.get(0).length()-1);
        int IndEP = UltimoToken.indexOf("=");
        String nombreENP = UltimoToken.substring(0, IndEP);
        String cantENP = UltimoToken.substring(IndEP+1,UltimoToken.length());
        llenarTabla[0] = nombreENP;
        llenarTabla[1] = cantENP;
        tabla.addRow(llenarTabla);
        txt_entrada.setText(Integer.toString(cont));
        b.close();

    }

    //lee todos los registros que sean EntradaNOPISEE y obtiene un group by, para luego mostrar resultado en la tabla 1
    void mostrarEntradaNoPisee(String archivo) throws FileNotFoundException, IOException {
        String[] cadenaNP = new String[2];
        String[] llenarTablaNP = new String[2];
        ArrayList<String> ArrENP = new ArrayList<>();
        DefaultTableModel tablaenp = new DefaultTableModel();
        tablaenp.addColumn("                INSTITUCIÓN");
        tablaenp.addColumn("                CANTIDAD");
        tb_entradanp.setModel(tablaenp);
        String texto = "EntradaNoPisee";
        int cont = 0;
        FileReader fenp = new FileReader(archivo);
        BufferedReader benp = new BufferedReader(fenp);
        ArrayList<String> lineasNP = new ArrayList<String>();
        String linea;
        int largo = 0;
        while ((cadenaNP[0] = benp.readLine()) != null) {
            int intIndex = cadenaNP[0].indexOf(texto);
            if (intIndex != -1) {
                int Indice2 = cadenaNP[0].indexOf("cgi-bin/");
                cadenaNP[0] = cadenaNP[0].substring(Indice2+8, cadenaNP[0].length());
                int Indice1 = cadenaNP[0].indexOf("/");
                String inst = cadenaNP[0].substring(0, Indice1);
                lineasNP.add(inst);
                cont = cont + 1;
            }
        }
        
        Map<String, Long> resul
                = lineasNP.stream().collect(
                        Collectors.groupingBy(
                                Function.identity(), Collectors.counting()
                        )
                );
        StringTokenizer TokenENP = new StringTokenizer(resul.toString(), ",");
            while (TokenENP.hasMoreTokens()) {
                ArrENP.add(TokenENP.nextToken());
            }
        for (int x = 0; x < ArrENP.size()-1; x++) {
            int IndENP = ArrENP.get(x).indexOf("=");
            String nombreENP = ArrENP.get(x).substring(1, IndENP);
            String cantENP = ArrENP.get(x).substring(IndENP+1,ArrENP.get(x).length());
            llenarTablaNP[0] = nombreENP;
            llenarTablaNP[1] = cantENP;
            tablaenp.addRow(llenarTablaNP);  
        }
        String UltimoToken = ArrENP.get(ArrENP.size()-1).substring(1,ArrENP.get(0).length()-1);
        int IndENP = UltimoToken.indexOf("=");
        String nombreENP = UltimoToken.substring(0, IndENP);
        String cantENP = UltimoToken.substring(IndENP+1,UltimoToken.length());
        llenarTablaNP[0] = nombreENP;
        llenarTablaNP[1] = cantENP;
        tablaenp.addRow(llenarTablaNP); 
        TXT_ENTRADANP.setText(Integer.toString(cont));
        benp.close();

    }
    
    
    //lee registro por registro y los clasifica según largo para luego insertarlos a una tabla oracle.
    String separarContenido(String archivo) throws FileNotFoundException, IOException, SQLException {
        String[] cadena = new String[2];
        String[] resu = new String[1];
        int cont = 0;
        ArrayList<String> ArrToken = new ArrayList<>();
        FileReader f = new FileReader(archivo);
        BufferedReader b = new BufferedReader(f);
        ////////////
            File arc = new File("RegistroLog");
            FileWriter w = new FileWriter(arc);
            BufferedWriter bw = new BufferedWriter(w);
            PrintWriter wr = new PrintWriter(bw); 
        ///////////                
        PreparedStatement pst;        
        while ((cadena[0] = b.readLine()) != null) {
            int intIndex = cadena[0].indexOf("?xml version=");
            if (intIndex == -1) {
                /*-------ENTRADAPISEE------*/
                int IndexEN = cadena[0].indexOf("EntradaPISEE");
                if (IndexEN != -1) {
                    long NTrans = 0;
                    String Fech = " ";
                    String Dom = ("EntPISEE");
                    String Serv = ("NA");
                    String Tramite = ("NA");
                    int Resp = 0;
                    String FcHrResp = ("01/01/0001");
                    String ParEnt = ("NA");
                    int IndEsp = cadena[0].indexOf(" ");
                    cadena[0] = cadena[0].substring(IndEsp, cadena[0].length());
                    cadena[0] = cadena[0].substring(IndEsp, cadena[0].length());
                    String Hr = cadena[0].substring(1, IndEsp + 6);
                    cadena[0] = cadena[0].substring(IndEsp + 6, cadena[0].length());
                    String IPDP = cadena[0].substring(1, IndEsp + 10);
                    int IndLin = cadena[0].indexOf("|");
                    cadena[0] = cadena[0].substring(IndLin, cadena[0].length());
                    StringTokenizer token = new StringTokenizer(cadena[0], "|");
                    while (token.hasMoreTokens()) {
                        ArrToken.add(token.nextToken());
                    }
                    String yy = ArrToken.get(0).substring(0, 4);
                    String mm = ArrToken.get(0).substring(4, 8);
                    String dd = ArrToken.get(0).substring(8, 10);
                    Fech = dd + mm + yy;
                    NTrans = Long.parseLong(ArrToken.get(1));
                    ArrToken.clear();
                    int Indice1 = cadena[0].lastIndexOf("/");
                    int Indice2 = cadena[0].indexOf("cgi-bin/");
                    String consum = cadena[0].substring(Indice2 + 8, Indice1);
                    String HRRespEnt = "NA";
                    try {
                        pst = cn.prepareStatement("INSERT INTO SRCEI.REG_LOG(V_RGL_DOM,V_RGL_FEC,V_RGL_HR,V_RGL_IP_DP,V_RGL_N_TRANS,V_RGL_CONSUM,V_RGL_SERV,V_RGL_PAR_ENT,V_RGL_TRAMITE,V_RGL_FECRESP,V_RGL_HRRESP,V_RGL_TIPO_RESP) VALUES(?,?,?,?,?,?,?,?,?,?,?,?)");
                        pst.setString(1, Dom);
                        pst.setString(2, Fech);
                        pst.setString(3, Hr);
                        pst.setString(4, IPDP);
                        pst.setLong(5, NTrans);
                        pst.setString(6, consum);
                        pst.setString(7, Serv);
                        pst.setString(8, ParEnt);
                        pst.setString(9, Tramite);
                        pst.setString(10, FcHrResp);
                        pst.setString(11,HRRespEnt);
                        pst.setInt(12, Resp);
                        pst.executeUpdate(); 
                        pst.close();
                    } catch (SQLException e) {                        
                        System.out.print(e);
                    }
                    
                    wr.append(Dom + "|" + Fech + "|" + Hr + "|" + IPDP + "|" + NTrans + "|" + consum + "|" + ParEnt + "|" + Serv + "|" + Tramite + "|" + FcHrResp +"|" + HRRespEnt + "|" + Resp + "\n");

                    System.out.print(Dom + "|" + Fech + "|" + Hr + "|" + IPDP + "|" + NTrans + "|" + consum + "|" + ParEnt + "|" + Serv + "|" + Tramite + "|" + FcHrResp + "|" + Resp + "\n");
                }

                /*-------ENTRADANOPISEE------*/
                int IndexENP = cadena[0].indexOf("EntradaNoPisee");
                    if (IndexENP != -1) {
                    long NTrans = 0;
                    String Fech = " ";
                    String Dom = ("EntNoPISEE");
                    String Serv = ("NA");
                    String Tramite = ("NA");
                    String HRRespEnt = "NA";
                    int Resp = 0;
                    String FcHrResp = ("01/01/0001");
                    String ParEnt = ("NA");
                    int IndEsp = cadena[0].indexOf(" ");
                    cadena[0] = cadena[0].substring(IndEsp, cadena[0].length());
                    cadena[0] = cadena[0].substring(IndEsp, cadena[0].length());
                    String Hr = cadena[0].substring(1, IndEsp + 6);
                    cadena[0] = cadena[0].substring(IndEsp + 6, cadena[0].length());
                    String IPDP = cadena[0].substring(1, IndEsp + 10);
                    int IndLin = cadena[0].indexOf("|");
                    cadena[0] = cadena[0].substring(IndLin, cadena[0].length());
                    StringTokenizer token = new StringTokenizer(cadena[0], "|");
                    while (token.hasMoreTokens()) {
                        ArrToken.add(token.nextToken());
                    }
                    String yy = ArrToken.get(0).substring(0, 4);
                    String mm = ArrToken.get(0).substring(4, 8);
                    String dd = ArrToken.get(0).substring(8, 10);
                    Fech = dd + mm + yy;
                    NTrans = Long.parseLong(ArrToken.get(1));
                    ArrToken.clear();
                    int Indice1 = cadena[0].lastIndexOf("/");
                    int Indice2 = cadena[0].indexOf("cgi-bin/");
                    cadena[0]  = cadena[0].substring(Indice2+8,Indice1+1);
                    int slash = cadena[0].indexOf("/");
                    String consum = cadena[0].substring(0, slash);
                    try {
                        pst = cn.prepareStatement("INSERT INTO SRCEI.REG_LOG(V_RGL_DOM,V_RGL_FEC,V_RGL_HR,V_RGL_IP_DP,V_RGL_N_TRANS,V_RGL_CONSUM,V_RGL_SERV,V_RGL_PAR_ENT,V_RGL_TRAMITE,V_RGL_FECRESP,V_RGL_HRRESP,V_RGL_TIPO_RESP) VALUES(?,?,?,?,?,?,?,?,?,?,?,?)");
                        pst.setString(1, Dom);
                        pst.setString(2, Fech);
                        pst.setString(3, Hr);
                        pst.setString(4, IPDP);
                        pst.setLong(5, NTrans);
                        pst.setString(6, consum);
                        pst.setString(7, Serv);
                        pst.setString(8, ParEnt);
                        pst.setString(9, Tramite);
                        pst.setString(10, FcHrResp);
                        pst.setString(11,HRRespEnt);
                        pst.setInt(12, Resp);
                        pst.executeUpdate(); 
                        pst.close();
                    } catch (SQLException e) {                        
                        System.out.print(e);
                    }
                    wr.append(Dom + "|" + Fech + "|" + Hr + "|" + IPDP + "|" + NTrans + "|" + consum + "|" + ParEnt + "|" + Serv + "|" + Tramite + "|" + FcHrResp +"|" + HRRespEnt + "|" + Resp + "\n");
                    System.out.print(Dom + "|" + Fech + "|" + Hr + "|" + IPDP + "|" + NTrans + "|" + consum + "|" + ParEnt + "|" + Serv + "|" + Tramite + "|" + FcHrResp +"|" + HRRespEnt + "|" + Resp + "\n");
                }
                /*-------REQUERIMIENTO------*/
                int IndexR = cadena[0].indexOf("Requerimiento");
                if (IndexR != -1) {
                    StringTokenizer tokenlarg = new StringTokenizer(cadena[0], "|");
                
                    while (tokenlarg.hasMoreTokens()) {
                        ArrToken.add(tokenlarg.nextToken());
                    }
                if (ArrToken.size() == 9) {
                    String DomReq = "REQ";                    
                    int IndEsp = ArrToken.get(0).indexOf(" ");
                    ArrToken.set(0,ArrToken.get(0).substring(IndEsp, ArrToken.get(0).length()));
                    ArrToken.set(0,ArrToken.get(0).substring(IndEsp, ArrToken.get(0).length()));
                    String HrReq = ArrToken.get(0).substring(1, IndEsp + 6);
                    ArrToken.set(0,ArrToken.get(0).substring(IndEsp + 6, ArrToken.get(0).length()));
                    String IPDPReq = ArrToken.get(0).substring(1, IndEsp + 10);
                    
                    String yyi = ArrToken.get(1).substring(0, 4);
                    String mmi = ArrToken.get(1).substring(4, 8);
                    String ddi = ArrToken.get(1).substring(8, 10);
                    String horaini = ArrToken.get(1).substring(11, 20).replace(" ","");
                    
                    String FechIniReq = ddi + mmi + yyi;                   
                    long NTransReq = Long.parseLong(ArrToken.get(2));
                   
                    String ConsumReq = ArrToken.get(4); 
                    String ServReq = ArrToken.get(5);
                    String TramiteReq = ArrToken.get(6);
                    String ParEntReq = ArrToken.get(8);
                        String yyr = ArrToken.get(7).substring(0, 4);
                        String mmr = ArrToken.get(7).substring(4, 8);
                        String ddr = ArrToken.get(7).substring(8, 10);
                        String HRRespReq = ArrToken.get(7).substring(11,19 ).replace(" ","");

                        String FcHrRespReq = (ddr + mmr + yyr);
                        try{
                            pst = cn.prepareStatement("INSERT INTO SRCEI.REG_LOG(V_RGL_DOM,V_RGL_FEC,V_RGL_HR,V_RGL_IP_DP,V_RGL_N_TRANS,V_RGL_CONSUM,V_RGL_SERV,V_RGL_PAR_ENT,V_RGL_TRAMITE,V_RGL_FECRESP,V_RGL_HRRESP,V_RGL_TIPO_RESP) VALUES(?,?,?,?,?,?,?,?,?,?,?,?)");
                            pst.setString(1, DomReq);
                            pst.setString(2, FechIniReq);
                            pst.setString(3, HrReq);
                            pst.setString(4, IPDPReq);
                            pst.setLong(5, NTransReq);
                            pst.setString(6, ConsumReq);
                            pst.setString(7, ServReq);
                            pst.setString(8, ParEntReq);
                            pst.setString(9, TramiteReq);
                            pst.setString(10, FcHrRespReq);
                            pst.setString(11,HRRespReq);
                            pst.setInt(12, 0);
                            pst.executeUpdate();
                            pst.close();
                        } catch (SQLException e) {
                            System.out.print(e);
                        }
                    wr.append(DomReq + "|" + FechIniReq + "|" + HrReq + "|" + IPDPReq + "|" + NTransReq + "|" + ConsumReq + "|"+  ParEntReq + "|" + ServReq +"|"+ TramiteReq +"|"+ FcHrRespReq +"|"+ HRRespReq +"|"+ 0 +"\n");
                    System.out.print(DomReq + "|" + ConsumReq + "|" + NTransReq + "|" + HrReq + "|"  + IPDPReq +"|"+ ServReq +"|"+ TramiteReq +"|" + ParEntReq + "|" + FechIniReq + horaini +"|"+ FcHrRespReq + "\n");
                }
                
                if (ArrToken.size() == 8) {
                    String DomReq = "REQ";                    
                    int IndEsp = ArrToken.get(0).indexOf(" ");
                    ArrToken.set(0,ArrToken.get(0).substring(IndEsp, ArrToken.get(0).length()));
                    ArrToken.set(0,ArrToken.get(0).substring(IndEsp, ArrToken.get(0).length()));
                    String HrReq = ArrToken.get(0).substring(1, IndEsp + 6);
                    ArrToken.set(0,ArrToken.get(0).substring(IndEsp + 6, ArrToken.get(0).length()));
                    String IPDPReq = ArrToken.get(0).substring(1, IndEsp + 10);
                    
                    String yyi = ArrToken.get(1).substring(0, 4);
                    String mmi = ArrToken.get(1).substring(4, 8);
                    String ddi = ArrToken.get(1).substring(8, 10);
                    String horaini = ArrToken.get(1).substring(11, 20).replace(" ","");
                    
                    String FechIniReq = ddi + mmi + yyi;                   
                    long NTransReq = Long.parseLong(ArrToken.get(2));
                   
                    String ConsumReq = ArrToken.get(3); 
                    String ServReq = ArrToken.get(4);
                    String TramiteReq = "NA";
                    String ParEntReq = ArrToken.get(5);
                        String FcHrRespReq = ("01/01/0001");
                        String HRRespReq = "NA";
                        try{
                            pst = cn.prepareStatement("INSERT INTO SRCEI.REG_LOG(V_RGL_DOM,V_RGL_FEC,V_RGL_HR,V_RGL_IP_DP,V_RGL_N_TRANS,V_RGL_CONSUM,V_RGL_SERV,V_RGL_PAR_ENT,V_RGL_TRAMITE,V_RGL_FECRESP,V_RGL_HRRESP,V_RGL_TIPO_RESP) VALUES(?,?,?,?,?,?,?,?,?,?,?,?)");
                            pst.setString(1, DomReq);
                            pst.setString(2, FechIniReq);
                            pst.setString(3, HrReq);
                            pst.setString(4, IPDPReq);
                            pst.setLong(5, NTransReq);
                            pst.setString(6, ConsumReq);
                            pst.setString(7, ServReq);
                            pst.setString(8, ParEntReq);
                            pst.setString(9, TramiteReq);
                            pst.setString(10, FcHrRespReq);
                            pst.setString(11,HRRespReq);
                            pst.setInt(12, 0);
                            pst.executeUpdate();
                            pst.close();
                        } catch (SQLException e) {
                            System.out.print(e);
                        }
                    wr.append(DomReq + "|" + FechIniReq + "|" + HrReq + "|" + IPDPReq + "|" + NTransReq + "|" + ConsumReq + "|"+  ParEntReq + "|" + ServReq +"|"+ TramiteReq +"|"+ FcHrRespReq +"|"+ HRRespReq +"|"+ 0 +"\n");
                    System.out.print(DomReq + "|" + ConsumReq + "|" + NTransReq + "|" + HrReq + "|"  + IPDPReq +"|"+ ServReq +"|"+ TramiteReq +"|" + ParEntReq + "|" + FechIniReq + horaini +"|"+ FcHrRespReq + "\n");
                }  
                
                if (ArrToken.size() == 7) {
                    String DomReq = "REQ";                    
                    int IndEsp = ArrToken.get(0).indexOf(" ");
                    ArrToken.set(0,ArrToken.get(0).substring(IndEsp, ArrToken.get(0).length()));
                    ArrToken.set(0,ArrToken.get(0).substring(IndEsp, ArrToken.get(0).length()));
                    String HrReq = ArrToken.get(0).substring(1, IndEsp + 6);
                    ArrToken.set(0,ArrToken.get(0).substring(IndEsp + 6, ArrToken.get(0).length()));
                    String IPDPReq = ArrToken.get(0).substring(1, IndEsp + 10);
                    
                    String yyi = ArrToken.get(1).substring(0, 4);
                    String mmi = ArrToken.get(1).substring(4, 8);
                    String ddi = ArrToken.get(1).substring(8, 10);
                    String horaini = ArrToken.get(1).substring(11, 20).replace(" ","");
                    
                    String FechIniReq = ddi + mmi + yyi;                   
                    long NTransReq = Long.parseLong(ArrToken.get(2));
                   
                    String ConsumReq = ArrToken.get(3); 
                    String ServReq = ArrToken.get(4);
                    String TramiteReq = ArrToken.get(5);
                    String ParEntReq = ArrToken.get(6);
                        String FcHrRespReq = ("01/01/0001");
                        String HRRespReq = "NA";
                        try{
                            pst = cn.prepareStatement("INSERT INTO SRCEI.REG_LOG(V_RGL_DOM,V_RGL_FEC,V_RGL_HR,V_RGL_IP_DP,V_RGL_N_TRANS,V_RGL_CONSUM,V_RGL_SERV,V_RGL_PAR_ENT,V_RGL_TRAMITE,V_RGL_FECRESP,V_RGL_HRRESP,V_RGL_TIPO_RESP) VALUES(?,?,?,?,?,?,?,?,?,?,?,?)");
                            pst.setString(1, DomReq);
                            pst.setString(2, FechIniReq);
                            pst.setString(3, HrReq);
                            pst.setString(4, IPDPReq);
                            pst.setLong(5, NTransReq);
                            pst.setString(6, ConsumReq);
                            pst.setString(7, ServReq);
                            pst.setString(8, ParEntReq);
                            pst.setString(9, TramiteReq);
                            pst.setString(10, FcHrRespReq);
                            pst.setString(11,HRRespReq);
                            pst.setInt(12, 0);
                            pst.executeUpdate();
                            pst.close();
                        } catch (SQLException e) {
                            System.out.print(e);
                        }
                    wr.append(DomReq + "|" + FechIniReq + "|" + HrReq + "|" + IPDPReq + "|" + NTransReq + "|" + ConsumReq + "|"+  ParEntReq + "|" + ServReq +"|"+ TramiteReq +"|"+ FcHrRespReq +"|"+ HRRespReq +"|"+ 0 +"\n");
                    System.out.print(DomReq + "|" + ConsumReq + "|" + NTransReq + "|" + HrReq + "|"  + IPDPReq +"|"+ ServReq +"|"+ TramiteReq +"|" + ParEntReq + "|" + FechIniReq + horaini +"|"+ FcHrRespReq + "\n");
                } 
                if (ArrToken.size() == 6) {
                    String DomReq = "REQ";                    
                    int IndEsp = ArrToken.get(0).indexOf(" ");
                    ArrToken.set(0,ArrToken.get(0).substring(IndEsp, ArrToken.get(0).length()));
                    ArrToken.set(0,ArrToken.get(0).substring(IndEsp, ArrToken.get(0).length()));
                    String HrReq = ArrToken.get(0).substring(1, IndEsp + 6);
                    ArrToken.set(0,ArrToken.get(0).substring(IndEsp + 6, ArrToken.get(0).length()));
                    String IPDPReq = ArrToken.get(0).substring(1, IndEsp + 10);
                    
                    String yyi = ArrToken.get(1).substring(0, 4);
                    String mmi = ArrToken.get(1).substring(4, 8);
                    String ddi = ArrToken.get(1).substring(8, 10);
                    String horaini = ArrToken.get(1).substring(11, 20).replace(" ","");
                    
                    String FechIniReq = ddi + mmi + yyi;                   
                    long NTransReq = Long.parseLong(ArrToken.get(2));
                   
                    String ConsumReq = ArrToken.get(3); 
                    String ServReq = ArrToken.get(4);
                    String TramiteReq = "NA";                    
                    String ParEntReq = ArrToken.get(5);
                        String FcHrRespReq = ("01/01/0001");
                        String HRRespReq = "NA";
                        try{
                            pst = cn.prepareStatement("INSERT INTO SRCEI.REG_LOG(V_RGL_DOM,V_RGL_FEC,V_RGL_HR,V_RGL_IP_DP,V_RGL_N_TRANS,V_RGL_CONSUM,V_RGL_SERV,V_RGL_PAR_ENT,V_RGL_TRAMITE,V_RGL_FECRESP,V_RGL_HRRESP,V_RGL_TIPO_RESP) VALUES(?,?,?,?,?,?,?,?,?,?,?,?)");
                            pst.setString(1, DomReq);
                            pst.setString(2, FechIniReq);
                            pst.setString(3, HrReq);
                            pst.setString(4, IPDPReq);
                            pst.setLong(5, NTransReq);
                            pst.setString(6, ConsumReq);
                            pst.setString(7, ServReq);
                            pst.setString(8, ParEntReq);
                            pst.setString(9, TramiteReq);
                            pst.setString(10, FcHrRespReq);
                            pst.setString(11,HRRespReq);
                            pst.setInt(12, 0);
                            pst.executeUpdate();
                            pst.close();
                        } catch (SQLException e) {
                            System.out.print(e);
                        }
                   wr.append(DomReq + "|" + FechIniReq + "|" + HrReq + "|" + IPDPReq + "|" + NTransReq + "|" + ConsumReq + "|"+  ParEntReq + "|" + ServReq +"|"+ TramiteReq +"|"+ FcHrRespReq +"|"+ HRRespReq +"|"+ 0 +"\n");
                   System.out.print(DomReq + "|" + ConsumReq + "|" + NTransReq + "|" + HrReq + "|"  + IPDPReq +"|"+ ServReq +"|"+ TramiteReq +"|" + ParEntReq + "|" + FechIniReq + horaini +"|"+ FcHrRespReq + "\n");
                }       
                
                
                if (ArrToken.size() == 5) {
                    String DomReq = "REQ";                    
                    int IndEsp = ArrToken.get(0).indexOf(" ");
                    ArrToken.set(0,ArrToken.get(0).substring(IndEsp, ArrToken.get(0).length()));
                    ArrToken.set(0,ArrToken.get(0).substring(IndEsp, ArrToken.get(0).length()));
                    String HrReq = ArrToken.get(0).substring(1, IndEsp + 6);
                    ArrToken.set(0,ArrToken.get(0).substring(IndEsp + 6, ArrToken.get(0).length()));
                    String IPDPReq = ArrToken.get(0).substring(1, IndEsp + 10);
                    
                    String yyi = ArrToken.get(1).substring(0, 4);
                    String mmi = ArrToken.get(1).substring(4, 8);
                    String ddi = ArrToken.get(1).substring(8, 10);
                    String horaini = ArrToken.get(1).substring(11, 20).replace(" ","");
                    
                    String FechIniReq = ddi + mmi + yyi;                   
                    long NTransReq = Long.parseLong(ArrToken.get(2));
                   
                    String ConsumReq = ArrToken.get(3); 
                    String ServReq = ArrToken.get(4);
                    String TramiteReq = "NA";
                    String ParEntReq = "NA";
                        String FcHrRespReq = ("01/01/0001");
                        String HRRespReq = "NA";
                        try{
                            pst = cn.prepareStatement("INSERT INTO SRCEI.REG_LOG(V_RGL_DOM,V_RGL_FEC,V_RGL_HR,V_RGL_IP_DP,V_RGL_N_TRANS,V_RGL_CONSUM,V_RGL_SERV,V_RGL_PAR_ENT,V_RGL_TRAMITE,V_RGL_FECRESP,V_RGL_HRRESP,V_RGL_TIPO_RESP) VALUES(?,?,?,?,?,?,?,?,?,?,?,?)");
                            pst.setString(1, DomReq);
                            pst.setString(2, FechIniReq);
                            pst.setString(3, HrReq);
                            pst.setString(4, IPDPReq);
                            pst.setLong(5, NTransReq);
                            pst.setString(6, ConsumReq);
                            pst.setString(7, ServReq);
                            pst.setString(8, ParEntReq);
                            pst.setString(9, TramiteReq);
                            pst.setString(10, FcHrRespReq);
                            pst.setString(11,HRRespReq);
                            pst.setInt(12, 0);
                            pst.executeUpdate();
                            pst.close();
                        } catch (SQLException e) {
                            System.out.print(e);
                        }
                    wr.append(DomReq + "|" + FechIniReq + "|" + HrReq + "|" + IPDPReq + "|" + NTransReq + "|" + ConsumReq + "|"+  ParEntReq + "|" + ServReq +"|"+ TramiteReq +"|"+ FcHrRespReq +"|"+ HRRespReq +"|"+ 0 +"\n");
                    System.out.print(DomReq + "|" + ConsumReq + "|" + NTransReq + "|" + HrReq + "|"  + IPDPReq +"|"+ ServReq +"|"+ TramiteReq +"|" + ParEntReq + "|" + FechIniReq + horaini +"|"+ FcHrRespReq + "\n");
                }       
                ArrToken.clear();
                }               
                               
                /*-------RESPUESTA------*/
                int IndexRes = cadena[0].indexOf("Respuesta");
                if (IndexRes != -1) {
                    StringTokenizer tokenlarg = new StringTokenizer(cadena[0], "|");
                
                    while (tokenlarg.hasMoreTokens()) {
                        ArrToken.add(tokenlarg.nextToken());
                    }
                if (ArrToken.size() == 9) {
                    String DomResp = "RESP";
                    int Resp = 0;
                    int IndEsp = ArrToken.get(0).indexOf(" ");
                    ArrToken.set(0,ArrToken.get(0).substring(IndEsp, ArrToken.get(0).length()));
                    ArrToken.set(0,ArrToken.get(0).substring(IndEsp, ArrToken.get(0).length()));
                    String HrResp = ArrToken.get(0).substring(1, IndEsp + 6);
                    ArrToken.set(0,ArrToken.get(0).substring(IndEsp + 6, ArrToken.get(0).length()));
                    String IPDPResp = ArrToken.get(0).substring(1, IndEsp + 10);
                    
                    String yyi = ArrToken.get(1).substring(0, 4);
                    String mmi = ArrToken.get(1).substring(4, 8);
                    String ddi = ArrToken.get(1).substring(8, 10);
                    String FechIniResp = ddi + mmi + yyi;
                    long NTransResp = Long.parseLong(ArrToken.get(2));
                   
                    String ConsumResp = ArrToken.get(4); 
                    String ServResp = ArrToken.get(5);
                    String TramiteResp = ArrToken.get(6);
                    String ParEntResp = "NA";
                        String yyr = ArrToken.get(7).substring(0, 4);
                        String mmr = ArrToken.get(7).substring(4, 8);
                        String ddr = ArrToken.get(7).substring(8, 10);
                        String FcHrRespResp = (ddr + mmr + yyr);
                        String HRRespResp = ArrToken.get(7).substring(11,19 ).replace(" ","");
                        
                        String TRESP = ArrToken.get(8);
                        if (TRESP.equals("Transaccion Exitosa") );
                        {
                            Resp = 1;
                        }
                        try {
                            pst = cn.prepareStatement("INSERT INTO SRCEI.REG_LOG(V_RGL_DOM,V_RGL_FEC,V_RGL_HR,V_RGL_IP_DP,V_RGL_N_TRANS,V_RGL_CONSUM,V_RGL_SERV,V_RGL_PAR_ENT,V_RGL_TRAMITE,V_RGL_FECRESP,V_RGL_HRRESP,V_RGL_TIPO_RESP) VALUES(?,?,?,?,?,?,?,?,?,?,?,?)");
                            pst.setString(1, DomResp);
                            pst.setString(2, FechIniResp);
                            pst.setString(3, HrResp);
                            pst.setString(4, IPDPResp);
                            pst.setLong(5, NTransResp);
                            pst.setString(6, ConsumResp);
                            pst.setString(7, ServResp);
                            pst.setString(8, ParEntResp);
                            pst.setString(9, TramiteResp);
                            pst.setString(10, FcHrRespResp);
                            pst.setString(11,HRRespResp);
                            pst.setInt(12, Resp);
                            pst.executeUpdate();
                            pst.close();
                        } catch (SQLException e) {
                            System.out.print(e);
                        }
                    wr.append(DomResp + "|" + FechIniResp + "|" + HrResp + "|" + IPDPResp + "|" + NTransResp + "|" + ConsumResp + "|"+  ParEntResp + "|" + ServResp +"|"+ TramiteResp +"|"+ FcHrRespResp +"|"+ HRRespResp +"|"+ Resp +"\n");
                    System.out.print(DomResp + "|" + ConsumResp + "|" + NTransResp + "|" + HrResp+ "|"  + IPDPResp +"|"+ ServResp +"|"+ TramiteResp +"|" + ParEntResp + "|" + FechIniResp +"|"+ FcHrRespResp + "|" + Resp + "\n");
                    }  
                if (ArrToken.size() == 6) {
                    String DomResp = "RESP";
                    int Resp = 0;
                    int IndEsp = ArrToken.get(0).indexOf(" ");
                    ArrToken.set(0,ArrToken.get(0).substring(IndEsp, ArrToken.get(0).length()));
                    ArrToken.set(0,ArrToken.get(0).substring(IndEsp, ArrToken.get(0).length()));
                    String HrResp = ArrToken.get(0).substring(1, IndEsp + 6);
                    ArrToken.set(0,ArrToken.get(0).substring(IndEsp + 6, ArrToken.get(0).length()));
                    String IPDPResp = ArrToken.get(0).substring(1, IndEsp + 10);
                    
                    String yyi = ArrToken.get(1).substring(0, 4);
                    String mmi = ArrToken.get(1).substring(4, 8);
                    String ddi = ArrToken.get(1).substring(8, 10);
                    String FechIniResp = ddi + mmi + yyi;
                    long NTransResp = Long.parseLong(ArrToken.get(2));
                   
                    String ConsumResp = ArrToken.get(3); 
                    String ServResp = ArrToken.get(4);
                    String TramiteResp = "NA";
                    String ParEntResp = "NA";
                    String FcHrRespResp = "01/01/0001";
                    String HRRespResp = "NA";
                        
                        String TRESP = ArrToken.get(5);
                        if (TRESP.equals("Transaccion Exitosa") || TRESP.equals("EXISTE INFORMACION PARA LA PATENTE CONSULTADA") || TRESP.equals("Terminaciones de Patentes Disponibles"));
                        {
                            Resp = 1;
                        }
                        try {
                            pst = cn.prepareStatement("INSERT INTO SRCEI.REG_LOG(V_RGL_DOM,V_RGL_FEC,V_RGL_HR,V_RGL_IP_DP,V_RGL_N_TRANS,V_RGL_CONSUM,V_RGL_SERV,V_RGL_PAR_ENT,V_RGL_TRAMITE,V_RGL_FECRESP,V_RGL_HRRESP,V_RGL_TIPO_RESP) VALUES(?,?,?,?,?,?,?,?,?,?,?,?)");
                            pst.setString(1, DomResp);
                            pst.setString(2, FechIniResp);
                            pst.setString(3, HrResp);
                            pst.setString(4, IPDPResp);
                            pst.setLong(5, NTransResp);
                            pst.setString(6, ConsumResp);
                            pst.setString(7, ServResp);
                            pst.setString(8, ParEntResp);
                            pst.setString(9, TramiteResp);
                            pst.setString(10, FcHrRespResp);
                            pst.setString(11,HRRespResp);
                            pst.setInt(12, Resp);
                            pst.executeUpdate();
                            pst.close();
                        } catch (SQLException e) {
                            System.out.print(e);
                        }
                    wr.append(DomResp + "|" + FechIniResp + "|" + HrResp + "|" + IPDPResp + "|" + NTransResp + "|" + ConsumResp + "|"+  ParEntResp + "|" + ServResp +"|"+ TramiteResp +"|"+ FcHrRespResp +"|"+ HRRespResp +"|"+ Resp +" \n");
                    System.out.print(DomResp + "|" + ConsumResp + "|" + NTransResp + "|" + HrResp+ "|"  + IPDPResp +"|"+ ServResp +"|"+ TramiteResp +"|" + ParEntResp + "|" + FechIniResp +"|"+ FcHrRespResp + "|" + Resp + "\n");     
                
                }
                ArrToken.clear();
            }
            cont = cont +1 ;   
            
        }
         TXT_CONT.setText(Integer.toString(cont));   
        }
        wr.close();
        bw.close();
        return null;
        
    }
    
    //función para copiar ejecutar loader y devolver salida de la maquina externa.
    String ListarSalida(String archivo){
        try {
    
            ConexionSFTP sshConnector = new ConexionSFTP(); 
            sshConnector.connect(USER, PASS, HOST, PUERTO);
            /* consigue nombre y directorio de archivo seleccionado*/
            String destino = ("/BDReplicacion/oradata/datos_txt/data_in/");

            sshConnector.AgregarArchivo(destino,archivo,archivo ); /*copia archivo a sftp*/
            sshConnector.AgregarArchivo(destino,archivo,archivo);
            
            sshConnector.crearSh(archivo); /*crea archivo .sh*/
            sshConnector.escribirSh(archivo);/*escribe en el sh*/
            sshConnector.sqlload(archivo); /*ejecuta el sh con el loader*/
            sshConnector.Borrar_temporales(archivo);
            sshConnector.disconnect();
            
        } catch (JSchException ex) {
            ex.printStackTrace();             
            JOptionPane.showMessageDialog(null, "Error de Conexión \n" + ex.getMessage());
            
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();             
            JOptionPane.showMessageDialog(null, "No hay Conexión Establecida \n" + ex.getMessage());
            
        } catch (IOException ex) {
            ex.printStackTrace();             
            JOptionPane.showMessageDialog(null, "Error de IO \n" + ex.getMessage());
            
        } catch (SftpException ex) {
            ex.printStackTrace();             
            JOptionPane.showMessageDialog(null, "Error de SFTP \n" + ex.getMessage());
            
        }
        
        
        return null;
    }

    /*void EncontrarCampos() {
        ArrayList<String> ArrToken = new ArrayList<String>();

        String input
                = "Jan  8 10:28:20 164.96.65.34 #012EntradaPISEE|2018-01-08T10: 28:22-03:00|276943185|https://163.247.64.184:443/cgi-bin/MINEDUC/WSMINEDUC.cgi";

        StringTokenizer token = new StringTokenizer(input, "|");
        while (token.hasMoreTokens()) {
            ArrToken.add(token.nextToken());
        }

        for (int contarr = 0; contarr < 4; contarr++) {
            System.out.println(ArrToken.get(contarr));
        }
    }*/
    
    //procedimiento para grabar ( no implementado aún).
    String Grabar(String domi, String feci, String hri, String ipdp, String parent, long numtra, String consumi, String servi, String ipcli, String tramit, String fechhrr, int tipor) {
        PreparedStatement pst;
        try {

            pst = cn.prepareStatement("INSERT INTO SRCEI.REG_LOG(V_RGL_DOM,V_RGL_FEC,V_RGL_HR,V_RGL_IP_DP,V_RGL_PAR_ENT,V_RGL_N_TRANS,V_RGL_CONSUM,V_RGL_SERV,V_RGL_IP_CLI,V_RGL_TRAMITE,V_RGL_FHRESP,V_RGL_TIPO_RESP) VALUES(?,?,?,?,?,?,?,?,?,?,?,?)");
            pst.setString(1, domi);
            pst.setString(2, feci);
            pst.setString(3, hri);
            pst.setString(4, ipdp);
            pst.setString(5, parent);
            pst.setLong(6, numtra);
            pst.setString(7, consumi);
            pst.setString(8, servi);
            pst.setString(9, ipcli);
            pst.setString(10, tramit);
            pst.setString(11, fechhrr);
            pst.setInt(12, tipor);
            pst.executeUpdate();
            cn.commit();

        } catch (Exception e) {
            System.out.print("NO INSERTO");
        }
        return null;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tb_monitoreo = new javax.swing.JTable();
        TXT_CONT = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tb_entradanp = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txt_entrada = new javax.swing.JTextField();
        TXT_ENTRADANP = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        tb_monitoreo.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        tb_monitoreo.setFont(tb_monitoreo.getFont().deriveFont(tb_monitoreo.getFont().getSize()+2f));
        jScrollPane1.setViewportView(tb_monitoreo);

        jLabel1.setText("Numero Total de Registros:");

        tb_entradanp.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        tb_entradanp.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane2.setViewportView(tb_entradanp);

        jLabel2.setText("EntradaPISEE: ");

        jLabel3.setText("EntradaNOPISEE");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 333, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(56, 56, 56)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 317, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(TXT_CONT, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(69, 69, 69)
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(txt_entrada, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel3)
                        .addGap(39, 39, 39)
                        .addComponent(TXT_ENTRADANP, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(22, 22, 22))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(TXT_CONT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(txt_entrada, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(TXT_ENTRADANP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 436, Short.MAX_VALUE)
                    .addComponent(jScrollPane2))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(monitoreo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(monitoreo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(monitoreo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(monitoreo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {

                try {
                    try {
                        new monitoreo().setVisible(true);
                    } catch (SQLException ex) {
                        Logger.getLogger(monitoreo.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(monitoreo.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField TXT_CONT;
    private javax.swing.JTextField TXT_ENTRADANP;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tb_entradanp;
    private javax.swing.JTable tb_monitoreo;
    private javax.swing.JTextField txt_entrada;
    // End of variables declaration//GEN-END:variables
}
