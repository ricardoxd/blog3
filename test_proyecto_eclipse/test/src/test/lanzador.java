package test;



import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import es.openhr.ws.PeTest.PeTestPortType;
import es.openhr.ws.WsAutenticacion.Autentication;
import es.openhr.ws.WsAutenticacion.WsAutenticacionPortType;
import factoriaWS.FactoriaWS;
import modelo.persistencia.ws.PasarelaConfiguracionWS;
import modelo.persistencia.ws.UrlWsdl;

public class lanzador {
	
	private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getRootLogger();

	public static void main(String[] args) {
		boolean result=true;
		log.info("Inicio ejecución de Test WS");
		log.info(System.getProperty("user.dir"));
        Properties Configuracion = new Properties();
        try {
            // cargamos el fichero de configuración
        	String file=System.getProperty("user.dir")+"/conexion.conf"; 
        	log.debug("Lectura del fichero de configuración: \""+file+"\"");
			Configuracion.load(new FileInputStream(file));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(lanzador.class.getName()).log(Level.SEVERE, null, ex);
			log.error("Exception (Message): "+ex.getMessage());
        } catch (IOException ex) {
            Logger.getLogger(lanzador.class.getName()).log(Level.SEVERE, null, ex);
			log.error("Exception (Message): "+ex.getMessage());
        }
        
        // configuración WS
        ArrayList<UrlWsdl> arrayListUrlWsdl = new ArrayList<UrlWsdl>();
        //test
        String urlWs = Configuracion.getProperty("ws.urlws_test");
        UrlWsdl uWobj=new UrlWsdl("test",urlWs);
        arrayListUrlWsdl.add(uWobj);
        urlWs = Configuracion.getProperty("ws.urlws_autenticar");
        uWobj=new UrlWsdl("autenticar",urlWs);
        arrayListUrlWsdl.add(uWobj);        
        
        PasarelaConfiguracionWS.getInstance(arrayListUrlWsdl);
                
        // instanciamos POJO Autenticar 
        String usuario = Configuracion.getProperty("ws.usuario");
        String clave = Configuracion.getProperty("ws.clave");
        String zona = Configuracion.getProperty("ws.zona");
        
        Autentication autenticationObj = new Autentication(usuario, clave, zona);
        FactoriaWS.getInstancia().setAutentication(autenticationObj);
        
        PeTestPortType petest = (PeTestPortType)FactoriaWS.getInstancia().getWS("test");
		WsAutenticacionPortType wsautenticar = (WsAutenticacionPortType)FactoriaWS.getInstancia().getWS("autenticar");
		
        try {
			if(petest.getTestOk())
				log.info("Respuesta WS Test: \""+petest.getTest()+"\"");
			else {
				result=false;
				log.error("ERROR de conexion con WS Test");
			}
			if(wsautenticar.autenticarObjCifrado(autenticationObj)){
				log.info("Autenticación WS satisfactoria");
				log.info("Respuesta WS Test autenticado: \""+wsautenticar.getTestAutenticado()+"\"");
			} else {
				result=false;
				log.error("ERROR de autenticación con WS");
			}
			if(result)
				lanzador.iniciar();
			else
				log.error("ERROR, no se inició el proceso, se ha producido algún problema en la inicialización del sistema.");
		} catch (RemoteException e) {
			e.printStackTrace();
			log.error("Exception (Message): "+e.getMessage());
		}
	}
	
	private static void iniciar(){
		
		log.info("Iniciando procedimiento de cualquier tipo de proceso que quieras hacer con OpenHR.");
		boolean result=true;
		// aquí añades tus calculos y procedimientos
		if(result)
			log.info("Procedimiento finalizado correctamente.");
		else
			log.error("Procedimiento finalizado con errores.");
	}
}