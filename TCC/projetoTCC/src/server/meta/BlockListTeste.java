package server.meta;

import dt.file.BlockInfoList;
import dt.file.BlockInfo;

public class BlockListTeste {
	static BlockInfoList teste() {
		BlockInfoList bList = new BlockInfoList(0L);
		
		//Teste de blocos. Modifique os valores pra teste =D
		//bList.add(new FileBlockInfo("hostName", porta, blockID));
		bList.add(new BlockInfo("hostName1", 10, 0));
		bList.add(new BlockInfo("hostName2", 10, 0));
		bList.add(new BlockInfo("hostName3", 10, 0));
		bList.add(new BlockInfo("hostName4", 10, 0));
		
		
		return bList;
	}
}
