import java.util.ArrayList;


public class SudokuSolver implements ISudokuSolver {

	int[][] puzzle;
	int size;

	ArrayList<ArrayList<Integer>> D; //= new ArrayList<ArrayList<Integer>>();
	
	public int[][] getPuzzle() {
		return puzzle;
	}

	public void setValue(int col, int row, int value) {
		puzzle[col][row] = value;
	}

	public void setup(int size1) {
		size = size1;
		puzzle = new int[size*size][size*size];
		D = new ArrayList<ArrayList<Integer>>(size*size*size*size);


		
		//Initialize each D[X]...
		for(int i = 0; i< size*size*size*size; i++){ //for each Variable
			ArrayList<Integer> domain = new ArrayList<Integer>(); // initialize the domain {0,1,2,3,4,5,6,7,8,9}
			for(int d = 0; d<10; d++){ 
				domain.add(d);
			}
			D.add(i, domain);
		}
		
	}


	public boolean solve() {
		ArrayList<Integer> asn = GetAssignment(puzzle);

		//Update D[X]
		updateDomains(asn);
		
		//INITIAL_FC
		if(!INITIAL_FC(asn)){
			return false;
		}

		
		//FC
		ArrayList<Integer> solution = FC(asn);
		// set puzzle[][] to the solution
			for (int i = 0; i <solution.size(); i++){
				setValue(GetRow(i),GetColumn(i), solution.get(i));
			}

		// Print result to terminal:
		System.out.println("Solution: ");
		System.out.println(solution.toString());


		// Print Doamins
		System.out.println("domains");
		System.out.println(D);
		
		return true;

	}
	/**
	 * method updates domains
	 *
	*/
	public void updateDomains(ArrayList<Integer> asn) {
		for(int i=0 ; i < asn.size(); i++){ // for each Variable from 0-80 
			if(asn.get(i) != 0){ // if the Variable has a Value
				ArrayList<Integer> relevantVariables = GetRelevantVariables(i);
				for(int j = 0; j< relevantVariables.size(); j++){ // for each relevant Variable 
					int index = D.get(relevantVariables.get(j)).indexOf(asn.get(i)); 
					if(index != -1){ // check that the Value is in the domain
						D.get(relevantVariables.get(j)).remove(index); // remove Value assigned to Variable from all the domains of relevant Variables
					}
				}
			}
		}
		// print the domains to the terminal:
		printDomain(D);
	}
	/**
	 * method prints domains
	 *
	*/
	public void printDomain(ArrayList<ArrayList<Integer>> domain) {
		for (int i = 0; i < domain.size(); i++){
			ArrayList<Integer> relevantVariables = domain.get(i);
			System.out.println("cell # " + i + " has domain: "+ relevantVariables.toString());
		}
	}

	public void readInPuzzle(int[][] p) {
		puzzle = p;
	}
	
	
		//---------------------------------------------------------------------------------
		//YOUR TASK:  Implement FC(asn)
		//---------------------------------------------------------------------------------
		public ArrayList FC(ArrayList<Integer> asn) {
			System.out.println("FC on asn: "+asn.toString());

			/***************************************
			if asn contains no 0 then 
				return asn
			X ← index of first 0 in asn
			Dold ←D
			for all V ∈ DX do
				if AC-FC(X, V ) then 
					asn[X] ← V
					R ←FC(asn)
					if R != fail then
						return R
					asn[X] ← 0
					D ← Dold
				else
					D ← Dold
			return fail
			***************************************/


			/***************************************
			if asn contains no 0 then 
				return asn
			X ← index of first 0 in asn
			***************************************/
			// initialize variable x
			int x=-1;
			// check the assignment for each cell in the grid
			// assign variable x to the first unassigned cell (value = 0)
			for(int i=0; i<asn.size();i++){
				if (asn.get(i)==0){
					x=i;
					break;
				}
			}
			// if asn contains no zeros, return asn
			if(x==-1){
				return asn;
			}

			/***************************************
			Dold ←D
			***************************************/
			// make a copy of the domain
			ArrayList<ArrayList<Integer>> dOld=new ArrayList<ArrayList<Integer>>(size*size*size*size);
			for(int variable = 0; variable < D.size(); variable++){
				ArrayList<Integer> tmpDomain = new ArrayList<Integer>();
				for(int value = 0; value < D.get(variable).size(); value++){
					tmpDomain.add(value, D.get(variable).get(value));
				}
				dOld.add(variable, tmpDomain);
			}
			/***************************************
			for all V ∈ DX do
			***************************************/
			// iterate through all the variables in the domain of x
			for (int i = 1; i < D.get(x).size(); i++){
				// varable v
				int v = D.get(x).get(i);
			/***************************************
				if AC-FC(X, V ) then asn[X] ← V
			***************************************/
				if(AC_FC(x, v)){		
			/***************************************
					asn[X] ← V
					R ←FC(asn)
			***************************************/
					// add value V to Variable X in the asn
					asn.set(x,v);
					// craate R from FC(asn)
					ArrayList<Integer> r = FC(asn);
			/***************************************
					if R != fail then
						return R
			***************************************/
					// if r NOT is empty
					if(r!=null){
						return r;
						
					}
					// else r is empty
			/***************************************
					asn[X] ← 0
					D ← Dold
			***************************************/
					// set value 0 to Variable X in the asn (redo the change so to speak)
					asn.set(x,0);
					// "reset D"
					for(int variable = 0; variable < dOld.size(); variable++){
						ArrayList<Integer> tmpDomain = new ArrayList<Integer>();
						for(int value = 0; value < dOld.get(variable).size(); value++){
							tmpDomain.add(value, dOld.get(variable).get(value));
						}
						D.set(variable, tmpDomain);
					}
				}
			/***************************************
				else
					D ← Dold
			***************************************/
				else{
					// "reset D"
					for(int variable = 0; variable < dOld.size(); variable++){
						ArrayList<Integer> tmpDomain = new ArrayList<Integer>();
						for(int value = 0; value < dOld.get(variable).size(); value++){
							tmpDomain.add(value, dOld.get(variable).get(value));
						}
						D.set(variable, tmpDomain);
					}
				}
			}
			/***************************************
			return fail
			***************************************/
			return null;//failure
		}
		
		//---------------------------------------------------------------------------------
		// CODE SUPPORT FOR IMPLEMENTING FC(asn)
		//
		// It is possible to implement FC(asn) by using only AC_FC function from below.
		// 
		// If you have time, I strongly reccomend that you implement AC_FC and REVISE from scratch
		// using only implementation of CONSISTENT algorithm and general utility functions. In my opinion
		// by doing this, you will gain much more from this exercise.
		//
		//---------------------------------------------------------------------------------
		
		
	
		//------------------------------------------------------------------
		//				AC_FC
		//
		// Implementation of acr-consistency for forward-checking AC-FC(cv).
		// This is a key component of FC algorithm, and the only function you need to 
		// use in your FC(asn) implementation
		//------------------------------------------------------------------
		public boolean AC_FC(Integer X, Integer V){
			//Reduce domain Dx
			D.get(X).clear();
			D.get(X).add(V);
			
			//Put in Q all relevant Y where Y>X
			ArrayList<Integer> Q = new ArrayList<Integer>(); //list of all relevant Y
			int col = GetColumn(X);
			int row = GetRow(X);
			int cell_x = row / size;
			int cell_y = col / size;
			
			//all variables in the same column
			for (int i=0; i<size*size; i++){
				if (GetVariable(i,col) > X) {
					Q.add(GetVariable(i,col));
				}
			}
			//all variables in the same row
			for (int j=0; j<size*size; j++){
				if (GetVariable(row,j) > X) {
					Q.add(GetVariable(row,j));
				}
			}
			//all variables in the same size*size box
			for (int i=cell_x*size; i<=cell_x*size + 2; i++) {
				for (int j=cell_y*size; j<=cell_y*size + 2; j++){
					if (GetVariable(i,j) > X) {
						Q.add(GetVariable(i,j));
					}
				}
			}
		
			//REVISE(Y,X)
			boolean consistent = true;
			while (!Q.isEmpty() && consistent){
				Integer Y = (Integer) Q.remove(0);
				if (REVISE(Y,X)) {
					consistent = !D.get(Y).isEmpty();
				}
			}
			return consistent;
		}	
		
		
		//------------------------------------------------------------------
		//				REVISE 
		//------------------------------------------------------------------
		public boolean REVISE(int Xi, int Xj){
			Integer zero = new Integer(0);
			
			assert(Xi >= 0 && Xj >=0);
			assert(Xi < size*size*size*size && Xj <size*size*size*size);
			assert(Xi != Xj);
			
			boolean DELETED = false;

			
			ArrayList<Integer> Di = D.get(Xi);
			ArrayList<Integer> Dj = D.get(Xj);	
			
			for (int i=0; i<Di.size(); i++){
				Integer vi = (Integer) Di.get(i);
				ArrayList<Integer> xiEqVal = new ArrayList<Integer>(size*size*size*size);	
				for (int var=0; var<size*size*size*size; var++){
					xiEqVal.add(var,zero);				
				}

				xiEqVal.set(Xi,vi);
				
				boolean hasSupport = false;	
				for (int j=0; j<Dj.size(); j++){
					Integer vj = (Integer) Dj.get(j);
					if (CONSISTENT(xiEqVal, Xj, vj)) {
						hasSupport = true;
						break;
					}
				}
				
				if (hasSupport == false) {
					Di.remove((Integer) vi);
					DELETED = true;
				}
				
			}
			
			return DELETED;
		}
				
		

		
		//------------------------------------------------------------------
		//CONSISTENT: 
		//
		//Given a partiall assignment "asn"  checks whether its extension with 
		//variable = val is consistent with Sudoku rules, i.e. whether it violates
		//any of constraints whose all variables in the scope have been assigned. 
		//This implicitly encodes all constraints describing Sudoku.
		//
		//Before it returns, it undoes the temporary assignment variable=val
		//It can be used as a building block for REVISE and AC-FC
		//
		//NOTE: the procedure assumes that all assigned values are in the range 
		// 		{0,..,9}. 
		//-------------------------------------------------------------------
		public boolean CONSISTENT(ArrayList<Integer> asn, Integer variable, Integer val) {
			Integer v1,v2;
			
			//variable to be assigned must be clear
			assert(asn.get(variable) == 0);
			asn.set(variable,val);

			//alldiff(col[i])
		 	for (int i=0; i<size*size; i++) {
		 		for (int j=0; j<size*size; j++) {
		 			for (int k=0; k<size*size; k++) {
			 			if (k != j) {
			 				v1 = (Integer) asn.get(GetVariable(i,j));
			 				v2 = (Integer) asn.get(GetVariable(i,k));
				 			if (v1 != 0 && v2 != 0 && v1.compareTo(v2) == 0) {
				 				asn.set(variable,0);
				 				return false;
				 			}
				 		}
		 			}
		 		}
		 	}
		

		 	
		 	//alldiff(row[j])
		 	for (int j=0; j<size*size; j++) {
		 		for (int i=0; i<size*size; i++) {
		 			for (int k=0; k<size*size; k++) {
			 			if (k != i) {
			 				v1 = (Integer) asn.get(GetVariable(i,j));
			 				v2 = (Integer) asn.get(GetVariable(k,j));
				 			if (v1 != 0 && v2 != 0 && v1.compareTo(v2) == 0) {
				 				asn.set(variable,0);			 				
				 				return false;
				 			}
			 			}
		 			}
		 		}
		 	}
		 	

		 	//alldiff(block[size*i,size*j])
		 	for (int i=0; i<size; i++) {
		 		for (int j=0; j<size; j++) {
		 			for (int i1 = 0; i1<size; i1++) {
		 				for (int j1=0; j1<size; j1++) {
		 					int var1 = GetVariable(size*i + i1, size*j + j1);
		 		 			for (int i2 = 0; i2<size; i2++) {
		 		 				for (int j2=0; j2<size; j2++) {
		 		 					int var2 = GetVariable(size*i+i2, size*j + j2);
		 		 					if (var1 != var2) {
		 				 				v1 = (Integer) asn.get(var1);
		 				 				v2 = (Integer) asn.get(var2);
		 		 			 			if (v1 != 0 && v2 != 0 && v1.compareTo(v2) == 0) {
		 					 				asn.set(variable,0);	 		 			 				
		 					 				return false;
		 					 			}
		 		 					}
		 		 				}
		 		 			}
	 
		 				}
		 			}
		 		}
		 	}

			asn.set(variable,0);
			return true;
		}	
		
		

	
		//------------------------------------------------------------------
		//						INITIAL_FC
		//------------------------------------------------------------------
		public boolean INITIAL_FC(ArrayList<Integer> anAssignment) {
			//Enforces consistency between unassigned variables and all 
			//initially assigned values; 
			for (int i=0; i<anAssignment.size(); i++){
				Integer V = (Integer) anAssignment.get(i);
				if (V != 0){
					ArrayList<Integer> Q = GetRelevantVariables(i);
					boolean consistent = true;
					while (!Q.isEmpty() && consistent){
						Integer Y = (Integer) Q.remove(0);
						if (REVISE(Y,i)) {
							consistent = !D.get(Y).isEmpty();
						}
					}	
					if (!consistent) return false;
				}
			}
			
			return true;
		}
		
		
	
		
		//------------------------------------------------------------------
		//						GetRelevantVariables
		//------------------------------------------------------------------
		public ArrayList<Integer> GetRelevantVariables(Integer X){
			//Returns all variables that are interdependent of X, i.e. 
			//all variables involved in a binary constraint with X
			ArrayList<Integer> Q = new ArrayList<Integer>(); //list of all relevant Y
			int col = GetColumn(X);
			int row = GetRow(X);
			int cell_x = row / size;
			int cell_y = col / size;
			
			//all variables in the same column
			for (int i=0; i<size*size; i++){
				if (GetVariable(i,col) != X) {
					Q.add(GetVariable(i,col));
				}
			}
			//all variables in the same row
			for (int j=0; j<size*size; j++){
				if (GetVariable(row,j) != X) {
					Q.add(GetVariable(row,j));
				}
			}
			//all variables in the same size*size cell
			for (int i=cell_x*size; i<=cell_x*size + 2; i++) {
				for (int j=cell_y*size; j<=cell_y*size + 2; j++){
					if (GetVariable(i,j) != X) {
						Q.add(GetVariable(i,j));
					}
				}
			}	
			
			return Q;
		}
		
		



		//------------------------------------------------------------------
		// Functions translating between the puzzle and an assignment
		//-------------------------------------------------------------------
		public ArrayList<Integer> GetAssignment(int[][] p) {
			ArrayList<Integer> asn = new ArrayList<Integer>();
			for (int i=0; i<size*size; i++) {
				for (int j=0; j<size*size; j++) {
					asn.add(GetVariable(i,j), new Integer(p[i][j]));
					if (p[i][j] != 0){
							//restrict domain
							D.get(GetVariable(i,j)).clear();
							D.get(GetVariable(i,j)).add(new Integer(p[i][j]));
						}
				}
			}
			return asn;
		}	
		
	
		public int[][] GetPuzzle(ArrayList asn) {
			int[][] p = new int[size*size][size*size];
			for (int i=0; i<size*size; i++) {
				for (int j=0; j<size*size; j++) {
					Integer val = (Integer) asn.get(GetVariable(i,j));
					p[i][j] = val.intValue();
				}
			}
			return p;
		}

	
		//------------------------------------------------------------------
		//Utility functions
		//-------------------------------------------------------------------
		public int GetVariable(int i, int j){
			assert(i<size*size && j<size*size);
			assert(i>=0 && j>=0);		
			return (i*size*size + j);	
		}	
		
		
		public int GetRow(int X){
			return (X / (size*size)); 	
		}	
		
		public int GetColumn(int X){
			return X - ((X / (size*size))*size*size);	
		}	
		
		
		
		
		
}
