package roundrobin.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import org.rrd4j.core.RrdDb;
import org.rrd4j.core.Util;

import roundrobin.database.StatManager;
import roundrobin.database.pojos.ControllerStat;
import roundrobin.database.pojos.PortStat;
import roundrobin.database.pojos.StatCollection;
import roundrobin.database.pojos.StatResponse;
import roundrobin.database.pojos.SwitchStat;

public class test {
	
	public static void main(String[] args) throws IOException, InterruptedException {
		
		StatManager stat = StatManager.getInstance();
		
		//Creating statCollection
		ControllerStat controllerStat = new ControllerStat();
		List<PortStat> listPort = new ArrayList<>();
		List<SwitchStat> listSwitch = new ArrayList<>();
		PortStat portStat;
		SwitchStat	switchStat;
		int t = 0; //numero de pushings --> 2 minutes
		int i=0,j=0;
		System.out.println("Inicio " + Util.getTime());
		for(i = 0; i<4;i++){
			switchStat = new  SwitchStat();
			switchStat.setSwitchId("00:00:00:00:00:00:00:0"+ i);
			switchStat.setByteCount(2345 + (3*i));
			switchStat.setFlowCount(1);
			switchStat.setPacketCount(23454+(16*i));
			
			listSwitch.add(switchStat);
		}
		
		for (i = 0; i < 4; i++) {

			for (j = 0; j < 5; j++) {
				portStat = new PortStat();
				portStat.setPortId("00:00:00:00:00:00:00:0" + i + ":0" + j);
				portStat.setReceivePackets(10 + i);
				portStat.setTransmitPackets(30 + (i * 2));
				portStat.setReceiveBytes(1600 + (i * 16));
				portStat.setTransmitBytes(3500 + (i * 32));
				portStat.setReceiveDropped(40 + i);
				portStat.setTransmitDropped(25 + i);
				portStat.setReceiveErrors(5 + i);
				portStat.setTransmitErrors(3 + i);
				portStat.setReceiveFrameErrors(3 + i);
				portStat.setReceiveOverrunErrors(2 + i);
				portStat.setReceiveCRCErrors(3 + i);
				portStat.setCollisions(4 + i);
				listPort.add(portStat);
			}
		}

		controllerStat.setCpuAvg(0.4);
		controllerStat.setMemoryPct(0.6);
		
		StatCollection statCollection = new StatCollection();
    	statCollection.setControllerStat(controllerStat);
    	statCollection.setPortStatCollection(listPort);
    	statCollection.setSwitchStatCollection(listSwitch);  
		
        while (true) {	
        	
        	stat.pushStat(statCollection);
        	Thread.sleep(1000);
     		t+=1;
     		System.out.println(t);
        }
        /*
        System.out.println("final " + Util.getTime());
        StatResponse container = new StatResponse();

        int x = 0;
        container = stat.getStat("floodLight", "CpuAvg", "AVERAGE", "minute");
        
        for(Double value : container.getValueAxxis()){
        	System.out.println("value " + x + ": " + value);
        	x++;
        }
       */
      
        
       
        
	}

}
