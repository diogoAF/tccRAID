package client;

import java.io.Console;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.InputMismatchException;
import java.util.Scanner;

import message.ResultType;


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
			System.out.println();
			System.out.println(c.getCurrPath());
			System.out.println();
			c.getCurrDir().list();
		    System.out.println(String.format("%1$3d", c.getCurrDir().dirCount()) +" diretorios");
		    System.out.println(String.format("%1$3d", c.getCurrDir().fileCount())+" arquivos");

			System.out.println();
		    System.out.println("Escolha a opcao desejada");
		    
		    System.out.println(String.format("%1$2d", Option.CREATEDIR) +": Criar diretorio");
		    System.out.println(String.format("%1$2d", Option.DELETEDIR) +": Deletar diretorio");
		    System.out.println(String.format("%1$2d", Option.RENAMEDIR) +": Renomear diretorio");
		    System.out.println(String.format("%1$2d", Option.OPENDIR)   +": Abrir diretorio");
		    System.out.println(String.format("%1$2d", Option.CLOSEDIR)  +": Fechar diretorio");
		    System.out.println();
            System.out.println(String.format("%1$2d", Option.CREATE)    +": Enviar arquivo");
		    System.out.println(String.format("%1$2d", Option.DELETE)    +": Deletar arquivo");
		    System.out.println(String.format("%1$2d", Option.RENAME)    +": Renomear arquivo");
		    System.out.println(String.format("%1$2d", Option.OPEN)      +": Abrir arquivo(leitura)");
		    System.out.println(String.format("%1$2d", Option.APPEND)    +": Abrir arquivo(escrita)");
		    System.out.println(String.format("%1$2d", Option.CLOSE)     +": Fechar arquivo");
		    
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
				create(con);
				break;
		        
            case(Option.DELETE):
				delete(con);
				break;

            case(Option.RENAME):
				rename(con);
				break;

            case(Option.OPEN):
                open(con);
                break;

            case(Option.APPEND):
                append(con);
                break;

            case(Option.CLOSE):
                close(con);
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
	
	private void criateDir(Console con) {
	    System.out.println();
	    System.out.println("Criar diretorio");
		String   dirName  = con.readLine("Nome do diretorio:\n>");
		
		if(dirName.isEmpty())
			return;
		
		try {
			int result = c.criateDir(dirName);
			
			if(result == ResultType.SUCCESS)
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
			
			if(result == ResultType.SUCCESS)
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
			
			if(result == ResultType.SUCCESS)
				System.out.println("Diretorio renomeado");
			else
				reportError(result);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void openDir(Console con) {
	    System.out.println();
	    System.out.println("Abrir diretorio");
		String   dirName  = con.readLine("Nome do diretorio:\n>");
		
		if(dirName.isEmpty())
			return;
			
		try {
			int result = c.openDir(dirName);
			
			if(result == ResultType.SUCCESS)
				System.out.println("Abrindo diretorio");
			else
				reportError(result);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void closeDir() {
		try {
			int result = c.closeDir();
			
			if(result == ResultType.SUCCESS)
				System.out.println("Fechando diretorio");
			else
				reportError(result);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void create(Console con) throws IOException {
		System.out.println();
		System.out.println("Criar arquivo");
		String name  = con.readLine("Nome ou local do arquivo:\n>");
		
		if(name.isEmpty())
			return;
		
		try {
			int result = c.create(name);
			
			if(result == ResultType.SUCCESS)
				System.out.println("Arquivo criado");
			else
				reportError(result);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void delete(Console con) {
	    System.out.println();
	    System.out.println("Deletar arquivo");
		String tgtName  = con.readLine("Nome do arquivo:\n>");

		if(tgtName.isEmpty())
			return;

		try {
			int result = c.delete(tgtName);
			
			if(result == ResultType.SUCCESS)
				System.out.println("Arquivo deletado");
			else
				reportError(result);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void rename(Console con) {
	    System.out.println();
	    System.out.println("Renomear arquivo");
		String tgtName  = con.readLine("Nome do arquivo:\n>");

		if(tgtName.isEmpty())
			return;
		String newName  = con.readLine("Novo nome do arquivo:\n>");
		
		if(newName .isEmpty())
			return;
		
		try {
			int result = c.rename(tgtName, newName);
			
			if(result == ResultType.SUCCESS)
				System.out.println("Arquivo renomeado");
			else
				reportError(result);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void open(Console con) {
	    System.out.println();
        System.out.println("Abrir arquivo para leitura");
        String   tgtName  = con.readLine("Nome do arquivo:\n>");
        
        if(tgtName.isEmpty())
            return;
            
        try {
            int result = c.open(tgtName);
            
            if(result == ResultType.SUCCESS)
                System.out.println("Abrindo arquivo para leitura");
            else
                reportError(result);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    

    private void append(Console con) {
        System.out.println();
        System.out.println("Abrir arquivo para escrita");
        String   tgtName  = con.readLine("Nome do arquivo:\n>");
        
        if(tgtName.isEmpty())
            return;
            
        try {
            int result = c.append(tgtName);
            
            if(result == ResultType.SUCCESS)
                System.out.println("Abrindo arquivo para escrita");
            else
                reportError(result);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void close(Console con) {
        if(c.getLockList().isEmpty()) {
            System.out.println("Nao existe nenhum arquivo aberto");
            return;
        }
        
        c.getLockList().list();
        System.out.println();
        System.out.println("Fechar arquivo");
        String   tgtIndex  = con.readLine("Numero do arquivo:\n");
        
        if(tgtIndex.isEmpty())
            return;
            
        try {
            int result = c.close(Integer.parseInt(tgtIndex));
            
            if(result == ResultType.SUCCESS)
                System.out.println("Arquivo fechado com sucesso");
            else
                reportError(result);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
	private void reportError(int result) {
		System.out.print("ERRO: ");
		switch (result) {
		case ResultType.NOSUCHFILE:
			System.out.println("arquivo local nao encontrado");
			break;
			
		case ResultType.FILEALREADYEXISTS:
			System.out.println("arquivo ja existe");
			break;
			
		case ResultType.FILENOTEXISTS:
			System.out.println("arquivo nao existe");
			break;

        case ResultType.FILELOCKED:
            System.out.println("arquivo esta bloqueado");
            break;

		case ResultType.DIRALREADYEXISTS:
			System.out.println("diretoroio ja existe");
			break;
			
		case ResultType.DIRNOTEXISTS:
			System.out.println("diretorio nao existe");
			break;

		case ResultType.DIRLOCKED:
			System.out.println("diretorio esta bloqueado");
			break;

        case ResultType.SERVERFAULT:
            System.out.println("numero de servidores insuficiente");
            break;
            
        case ResultType.WRONGINDEX:
            System.out.println("o indice esta errado");
            break;
            
		case ResultType.FAILURE:
			System.out.println("falha ao executar operacao");
			break;
			
		default:
			System.out.println("resultado desconhecido: " + result);
			break;
		}
	}

}