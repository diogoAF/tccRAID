package client;

import files.Metadata;
import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.InputMismatchException;
import java.util.Scanner;
import result.Result;


public class ClientConsole {
	ClientDFS c;
	
	public static void main(String[] args) throws IOException {
    	if(args.length < 1) {
            System.out.println("Use: java ClientConsole <processId>");
            System.exit(-1);
    	}
    	ClientDFS     c  = new ClientDFS(Integer.parseInt(args[0]));
    	ClientConsole cc = new ClientConsole(c);
        
    	cc.run();
    	
    	//cc.test();

		c.exit();
		System.out.println("Programa terminado");
		System.exit(0);
    }
	
	private ClientConsole(ClientDFS c) {
		this.c = c;
	}
	
	private void run() throws IOException {
		Scanner sc   = new Scanner(System.in);
        boolean exit = false;
        Console con  = System.console();
        
		do {
			clearConsole();
			System.out.println();
			System.out.println(c.getCurrPath());
			System.out.println();
			c.getCurrent().list();
		    System.out.println(String.format("%1$3d", c.getCurrent().dirCount()) +" diretorios");
		    System.out.println(String.format("%1$3d", c.getCurrent().fileCount())+" arquivos");

			System.out.println();
		    System.out.println("Escolha a opcao desejada");
		    
		    System.out.println(Option.OPENDIR   +": Abrir diretorio");
		    System.out.println(Option.CREATEDIR +": Criar diretorio");
		    System.out.println(Option.DELETEDIR +": Deletar diretorio");
		    System.out.println(Option.RENAMEDIR +": Renomear diretorio");
		    System.out.println(Option.CLOSEDIR  +": Fechar diretorio");
                    System.out.println(Option.CREATE  +": Enviar arquivo");
		    
		    System.out.println();
		    System.out.println(Option.EXIT+": Terminar");
		    
		    System.out.print(">");
		    
			int option;
			try {
				option = sc.nextInt();
			} catch (InputMismatchException e) {
				sc.nextLine();
				option = -1;
			}
			
			switch(option) {
			case(Option.OPENDIR):
				openDir(con);
				break;
			
			case(Option.CREATEDIR):
				criateDir(con);
				break;

			case(Option.DELETEDIR):
				deleteDir(con);
				break;

			case(Option.RENAMEDIR):
				renameDir(con);
				break;

			case(Option.CLOSEDIR):
				closeDir();
				break;
                            
                        case(Option.CREATE):
				createFile(con);
				break;
			
			case(Option.EXIT):
				exit = true;
				break;
				
			default:
				System.out.println("ERRO: opcao invalida");
				break;
			}
			
			System.out.println();
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
	            System.exit(-1);
			}
        } while(!exit);
		
		sc.close();
	}
	
	private void openDir(Console con) {
	    System.out.println();
	    System.out.println("Abrir diretorio");
		String   dirName  = con.readLine("Nome do diretorio:\n>");
		
		if(dirName.isEmpty())
			return;
			
		try {
			int result = c.openDir(dirName);
			
			if(result == Result.SUCCESS)
				System.out.println("Abrindo diretorio");
			else
				reportError(result);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void criateDir(Console con) {
	    System.out.println();
	    System.out.println("Criar diretorio");
		String   dirName  = con.readLine("Nome do diretorio:\n>");
		
		if(dirName.isEmpty())
			return;

		Metadata metadata = new Metadata(System.currentTimeMillis());
		
		try {
			int result = c.criateDir(dirName, metadata);
			
			if(result == Result.SUCCESS)
				System.out.println("Diretorio criado");
			else
				reportError(result);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void deleteDir(Console con) {
	    System.out.println();
	    System.out.println("Deletar diretorio");
		String   dirName  = con.readLine("Nome do diretorio:\n>");

		if(dirName.isEmpty())
			return;

		try {
			int result = c.deleteDir(dirName);
			
			if(result == Result.SUCCESS)
				System.out.println("Diretorio deletado");
			else
				reportError(result);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void renameDir(Console con) {
	    System.out.println();
	    System.out.println("Renomear diretorio");
		String   dirName  = con.readLine("Nome do diretorio:\n>");

		if(dirName.isEmpty())
			return;
		String   newName  = con.readLine("Novo nome do diretorio:\n>");

		if(newName.isEmpty())
			return;


		try {
			int result = c.renameDir(dirName, newName);
			
			if(result == Result.SUCCESS)
				System.out.println("Diretorio renomeado");
			else
				reportError(result);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void closeDir() {
		try {
			int result = c.closeDir();
			
			if(result == Result.SUCCESS)
				System.out.println("Fechando diretorio");
			else
				reportError(result);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void reportError(int result) {
		System.out.print("ERRO: ");
		switch (result) {
		case Result.DIRALREADYEXISTS:
			System.out.println("diretoroio ja existe");
			break;
			
		case Result.DIRNOTEXISTS:
			System.out.println("diretorio nao existe");
			break;

		case Result.DIRLOCKED:
			System.out.println("diretorio esta bloqueado");
			break;
			
		case Result.FAILURE:
			System.out.println("falha ao executar operacao");
			break;
			
		default:
			System.out.println("resultado desconhecido");
			break;
		}
	}
	
	@SuppressWarnings("unused")
	private void test() {
        File      file     = new File("testfile");
  		Metadata  metadata = new Metadata(file);
  		String 	  path     = Paths.get(file.getAbsolutePath()).toString();
  		
		try {
        	byte[] resp;
			
        	resp = c.create(path, metadata);
	        System.out.println("Resposta create: "+new String(resp));

        	resp = c.delete(path);
	        System.out.println("Resposta delete: "+new String(resp));

        	resp = c.open(path);
	        System.out.println("Resposta open: "+new String(resp));

        	resp = c.append(path);
	        System.out.println("Resposta append: "+new String(resp));
	        
	        
	        
	        
		} catch (IOException e) {
			e.printStackTrace();
            System.exit(-1);
		}
	}

	public static void clearConsole() {
	    try {
	        String os = System.getProperty("os.name");

	        if (os.contains("Windows")) {
	            Runtime.getRuntime().exec("cls");
	        } else {
	            Runtime.getRuntime().exec("clear");
	        }
	    }
	    catch (Exception e) {
	    	e.printStackTrace();
            System.exit(-1);
	    }
	}

    private void createFile(Console con) throws IOException {
        String hostName = "127.0.0.1";
        int portNumber = 4400;
        System.out.println();
        System.out.println("Enviar arquivo");
        //String   filePath  = con.readLine("Path do arquivo:\n>");
        Socket clientSocket = new Socket(hostName, portNumber);
        System.out.println("O cliente se conectou ao servidor!");
     
        Scanner scanner = new Scanner(System.in);
        PrintStream outStream = new PrintStream(clientSocket.getOutputStream());

        while (scanner.hasNextLine()) {
          outStream.println(scanner.nextLine());
        }
        
        outStream.close();
        scanner.close();
        clientSocket.close();
		
                /*
		if(filePath.isEmpty())
			return;
			
		try {
			int result = c.openFile(filePath);
			
			if(result == Result.SUCCESS)
				System.out.println("Abrindo diretorio");
			else
				reportError(result);

		} catch (IOException e) {
			e.printStackTrace();
		}
                        */
    }
}