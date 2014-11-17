import java.util.Random;
public class AI_Anh extends AIModule
{
	private int depthLimit = 7;
	private int maxMove;

	public void getNextMove(final GameStateModule game)   {
		miniMax(game, depthLimit, 1);
//		System.out.println(game.getCoins());        

		chosenMove = maxMove;
                //printBoard(game);
	}
        
        // -18
        private static int[] test = {-39, 20, 34, 49, -33, -17, 12, 
                                      45, 42, 13, 9, -18, 35, -7, 
                                      0, 29, 19, -6, -21, 35, 7, 
                                      17, 4, -49, 27, -24, 45, 35, 
                                      29, -37, 46, 8, 3, -11, -1, 
                                      -29, 21, 25, -16, -42, 16, 27, 
                                      -44, 25, -14, 16, 35, 19, -37};
        
	private static int[][] evalTable = 
		{{3, 4, 5,  9,  5,  4, 3},	
		 {4, 6, 8,  10, 8,  6, 4},
		 {5, 8, 11, 13, 11, 8, 5},
		 {5, 8, 11, 13, 11, 8, 5},
 		 {4, 6, 8,  10, 8,  6, 4},		  				  		
		 {3, 4, 5,  9,  5,  4, 3}};
	
	public int evaluateFunction(final GameStateModule game) {
		int utilityValue = 69;
		
		for (int i = 0; i < 6; i++)
			for (int j = 0; j < 7; j++)
				if (game.getAt(j,i) == 1)
					utilityValue += evalTable[i][j];
				else if (game.getAt(j,i) == 2)
					utilityValue -= evalTable[i][j];
		return  utilityValue;

}

	private int miniMax(final GameStateModule game, int depth,int playerIndex){

		if(game.isGameOver())
			if(game.getWinner() == 1)
				return 276;
			else return -276;
		
		
		if (game.getCoins() == 42 || depth == 0)
			return evaluateFunction(game);		

		depth--;
			
		if(playerIndex ==  1){ // MAX
			int max = Integer.MIN_VALUE;
                        int move = 0;
			for(int i = 0; i < 7; i++)
				if(game.canMakeMove (i))
				{
					game.makeMove(i);
					int v = miniMax(game, depth, 2);
					if (max < v){
						max = v;
						move = i;
					}
					game.unMakeMove();
				}
                        maxMove = move;
			return max;
		 } 
		else { // MIN
			int min = Integer.MAX_VALUE;
			for(int i = 0; i < 7; i++)
				if(game.canMakeMove (i)){
					game.makeMove(i);
					int v = miniMax(game, depth, 1);
					if (min > v){
						min = v;
					}
					game.unMakeMove();
				}
			return min;
		}
	}
        
        private void printBoard(final GameStateModule game) {
            for(int i=5; i>-1; i--) {
                for(int j=0; j<7; j++) {
                    if(game.getAt(j,i) == 1)
                        System.out.print("X ");
                    else if(game.getAt(j,i) == 2)    
                        System.out.print("O ");
                    else   
                        System.out.print(". ");
                        
                    
                }
                System.out.println();
            }
            System.out.println();
            System.out.println();
        }
}
