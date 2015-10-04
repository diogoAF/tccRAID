package test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

import server.ServerInfo;
import server.meta.ServerList;

public class Test1 {

	public static void main(String[] args) {
		ServerList list = new ServerList();
		Integer i = 1;
        long currTime = System.currentTimeMillis();

		list.add(new ServerInfo(i.toString(), i*100, 100, 11, i*10, currTime));i++;
		list.add(new ServerInfo(i.toString(), i*100, 100, 4, i*10, currTime));i++;
		list.add(new ServerInfo(i.toString(), i*100, 100, 4, i*10, currTime));i++;
		list.add(new ServerInfo(i.toString(), i*100, 100, 25, i*10, currTime));i++;
		list.add(new ServerInfo(i.toString(), i*100, 100, 12, i*10, currTime));i++;
		list.add(new ServerInfo(i.toString(), i*100, 100, 80, i*10, currTime));i++;
		list.add(new ServerInfo(i.toString(), i*100, 100, 10, i*10, currTime));i++;
		list.add(new ServerInfo(i.toString(), i*100, 100, 5, i*10, currTime));i++;
		
		
		list.print();
		list.nexts();

		System.out.println();
		list.print();
		
	}
	
	

}
