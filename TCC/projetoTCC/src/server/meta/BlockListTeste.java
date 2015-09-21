package server.meta;

import dt.file.BlockList;
import dt.file.FileBlockInfo;

public class BlockListTeste {
	static BlockList teste() {
		BlockList bList = new BlockList();
		
		//Teste de blocos. Modifique os valores pra teste =D
		//bList.add(new FileBlockInfo("hostName", porta, blockID));
		bList.add(new FileBlockInfo("hostName1", 10, 0));
		bList.add(new FileBlockInfo("hostName2", 10, 0));
		bList.add(new FileBlockInfo("hostName3", 10, 0));
		bList.add(new FileBlockInfo("hostName4", 10, 0));
		
		
		return bList;
	}
}
