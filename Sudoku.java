import java.lang.*;
import java.io.*;
import java.util.*;


public class Sudoku {
    class invalidGrid extends Exception {};
    class invalidInsertion extends Exception {};
    class invalidCommand extends Exception {};
    public int[][] findGrid (String dir) throws IOException {
        //lit le fichier indiqué par dir et vérifie que c’est une grille valide
        //retourne grille remplie si valide, une grille vide sinon, sous forme de tableau
        File grid= new File (dir);
        FileReader f = new FileReader (grid);
        BufferedReader buffer = new BufferedReader(f);
        String line=buffer.readLine();
        int[][] sudoku= new int[9][9];
        for (int i=0; i<9; i++) {Arrays.fill(sudoku[i], 0);} //initialize le tableau à zero,
        //permet de détecté les case "vide" facilement
        boolean done=false, legalgrid=true;
        try{
            for (int n=0; !done; n+=4){
                //getNumericValue() retourne des valeurs négatives si le caractère 
                //n'est pas une valeur numérique or une lettre
                int x=Character.getNumericValue(line.charAt(n)); 
                int y=Character.getNumericValue(line.charAt(n+1));
                int z=Character.getNumericValue(line.charAt(n+2));
                if (x>8||y>8||z>9) {done=true; legalgrid=false;}
                //index hors bornes pour leur valeurs possibles 
                //(ou les caractères ne sont pas des chiffres,
                //et leurs valeurs numériques sont de 10 à 35)
                else if (x<0||y<0||z<1) {done=true; legalgrid=false;}
                //index hors bornes pour leurs valeurs possibles
                else sudoku[x][y]=z;
            }
        } catch (NullPointerException | StringIndexOutOfBoundsException e) {
            done=true;
        }
        f.close();
        buffer.close();
        if (!legalgrid) {
            System.out.print("The file "+dir+" is not a valid sudoku grid");
            sudoku= new int[0][0];
        }
        return sudoku;
    };
    protected boolean checkPresence (int[][] grid, int i, int j, int k){
        //retourne false si la val n'est pas dans la grille, true si elle est dans la grille
        //vérifie la présence de k dans la ligne i et la colonne j de la grille grid
        //dans l'idée d'insérer la valeur k à cette position (insertVal),
        //ou que la valeur k s'y trouve déjà (checkGris)
        boolean presence=false;
        checkCol :
        for (int a=0; a<9; a++){ 
            if (a!=i){
                if (grid[a][j]==k) {
                    presence = true;
                    break checkCol;
                }
            }
        }
        checkLine :
        if (!presence){ //si la présence est passez à true en checkant les colonne,
                        //il est inutile de checker les lignes
            for (int b=0; b<9; b++){ 
                if (b!=j){
                    if (grid[i][b]==k) {
                        presence = true;
                        break checkLine;
                    }
                }
            }
        }
        return presence;
    };
    public boolean checkGrid (int[][] grid) throws invalidGrid {
        //retourne false si la grille ne respecte les contraintes,
        //true si elles sont respectées
        boolean notValidGrid=false;
            for (int i=0; i<9; i++){
                for (int j=0; j<9; j++){
                    if (grid[i][j]!=0){
                        notValidGrid=checkPresence(grid, i, j, grid[i][j]);
                        if (notValidGrid) throw new invalidGrid();
                    }
                }
            }
        return (!notValidGrid);
    };
    public boolean insertVal (int[][] grid, int val, int posx, int posy) throws invalidInsertion {
        //retorune false si échec d'insertion, true si insertion réussie
        boolean insertion;
        if (val<1||val>9||posy<0||posy>8||posx<0||posx>8) {
            insertion=false;
            throw new invalidInsertion();
        }
        if (!checkPresence(grid, posx, posy, val)) {
            insertion=true;
            grid[posx][posy]=val;
        } else {
            insertion=false;
            throw new invalidInsertion();
        }
        return insertion;
    };
    public void affichage (int[][] grid){
        //transpose la grille
        int[][] transpo = new int[9][9];
        for (int i=0; i<9; i++){
            for (int j=0; j<9; j++){
                transpo[i][j]=grid[j][i];
            }
        }
        //imprime la transposé dans la console
        String lineSep = new String("-------------");
        for (int x=0; x<9; x++){
            if (x==0 || x==3 || x==6) System.out.println(lineSep);
            System.out.println("|"+grid[x][0]+grid[x][1]+grid[x][2]+"|"+grid[x][3]+grid[x][4]+grid[x][5]+"|"+grid[x][6]+grid[x][7]+grid[x][8]+"|");
        }
        System.out.println(lineSep);
    };
    public void saveChanges (int[][] grid, String dir) throws IOException {
        //dir est le dossier parent où enregistrer la grille
        File newgrid = new File(dir, "newfile.txt");
        FileWriter writer = new FileWriter (newgrid);
        for (int i=0; i<9; i++){
            for (int j=0; j<9;j++){
                if (grid[i][j]!=0) {
                    writer.write(String.valueOf(i));
                    writer.write(String.valueOf(j));
                    writer.write(String.valueOf(grid[i][j]));
                    writer.write(" ");
                }
            }
        }
        writer.close();
    };
    public void Sudoku(){};
    public void throwIC() throws invalidCommand {throw new invalidCommand();};
    
    public static void main(String arg[]) throws Exception {
        try{
            int continueCommand;
            Sudoku sudoku=new Sudoku();
            playOnNewFile:
            do{
                System.out.println("What file do you wish to play on?");
                BufferedReader fileNameBuffer = new BufferedReader(new InputStreamReader(System.in));
                String fileName=fileNameBuffer.readLine();
                int[][] grid1=sudoku.findGrid(fileName);
                playAgain:
                do{
                    System.out.println("Do you wish to (enter numbre only):\n1.Play on the grid\n2.Check if the constraints are respected\n3.Save your grid\n4.Print out your grid");
                    BufferedReader commandBuffer = new BufferedReader(new InputStreamReader(System.in));
                    int command=Character.getNumericValue((char)commandBuffer.read());
                    if (command==1){
                        //Play
                        System.out.println("Please enter the line number");
                        BufferedReader xBuffer=new BufferedReader(new InputStreamReader(System.in));
                        int posx=Character.getNumericValue((char)xBuffer.read());
                        System.out.println("Please enter the column number");
                        BufferedReader yBuffer=new BufferedReader(new InputStreamReader(System.in));
                        int posy=Character.getNumericValue((char)yBuffer.read());
                        System.out.println("Please enter the value");
                        BufferedReader zBuffer=new BufferedReader(new InputStreamReader(System.in));
                        int valz=Character.getNumericValue((char)zBuffer.read());
                        sudoku.insertVal(grid1, valz, posx, posy);
                        System.out.println("Succesful insertion of "+valz+" in line "+posx+" and column "+posy);
                    } else if (command==2){
                        //CheckGrid
                        try{
                            sudoku.checkGrid(grid1);
                            System.out.println("This grid is a valid Sudoku grid");
                        }catch (invalidGrid g){
                            System.out.println("This grid is not a valid Sudoku grid");
                        }
                    } else if (command==3){
                        //SaveGrid
                        System.out.println("Where would you like your file saved?");
                        BufferedReader parentDirBuffer=new BufferedReader(new InputStreamReader(System.in));
                        String parentDir=parentDirBuffer.readLine();
                        sudoku.saveChanges(grid1, parentDir);
                        System.out.println("Grid succesfully saved in "+parentDir+" as newfile.txt");
                    } else if (command==4){
                        //PrintGrid
                        sudoku.affichage(grid1);
                    } else sudoku.throwIC();
                    do{
                        System.out.println("Do you wish to continue?\nEnter 1 to continue on this grid, 2 to charge up another grid, 3 to stop");
                        BufferedReader continueBuffer=new BufferedReader(new InputStreamReader(System.in));
                        continueCommand=Integer.parseInt(continueBuffer.readLine());
                        if (continueCommand==1){
                            continue playAgain;
                        } else if (continueCommand==2) {
                            continue playOnNewFile;
                        } else if (continueCommand==3) {
                            continueBuffer.close();
                            commandBuffer.close();
                            fileNameBuffer.close();
                            return;
                        } else sudoku.throwIC();
                    } while (continueCommand!=1 || continueCommand!=2 || continueCommand!=3);
                } while (continueCommand<2);
            } while (continueCommand<3);
        }catch (FileNotFoundException fnf) {
            System.out.println("Unable to find file");
        }catch (IOException io){
            System.out.println("Invalid file/directory entered");
        }catch (invalidCommand c){
            System.out.println("This command is invalid, please enter a number as specified in the instruction");
        }catch (invalidInsertion i) {
            System.out.println("This insertion is not possible");
        }
    };
}