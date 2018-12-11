package Model;

import Client.IClientStrategy;
import IO.MyDecompressorInputStream;
import algorithms.mazeGenerators.Maze;

import algorithms.search.Solution;
import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import Server.Server;
import Server.ServerStrategyGenerateMaze;
import Server.ServerStrategySolveSearchProblem;
import Client.*;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Observable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * this is the Model class, it has the maze
 * and all the code behind the VIEW
 * @authors ben moalem & idan revah
 */

public class MyModel extends Observable implements IModel {

    //Server and Threads
    private ExecutorService threadPool = Executors.newCachedThreadPool();
    private Server mazeGeneratingServer;
    private Server solveSearchProblemServer;

    //Maze info
    private Maze maze;
    private Solution solution;
    private int characterPositionRow;
    private int characterPositionColumn;

    public MyModel() {
        mazeGeneratingServer = new Server(5400, 1000, new ServerStrategyGenerateMaze());
        solveSearchProblemServer = new Server(5401, 1000, new ServerStrategySolveSearchProblem());
        startServers();
    }



    //-----------//
    //--Servers--//
    //-----------//

    public void startServers() {
        solveSearchProblemServer.start();
        mazeGeneratingServer.start();
    }

    public void stopServers() {
        mazeGeneratingServer.stop();
        solveSearchProblemServer.stop();
        threadPool.shutdown();
        Platform.exit();
        System.exit(0);
    }



    //----------------------//
    //---Generate & Solve---//
    //----------------------//

    @Override
    public void generateMaze(int width, int height) {
        //Generate maze
        threadPool.execute(() -> {
            CommunicateWithServer_MazeGenerating(width, height);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            setChanged();
            notifyObservers();
        });
    }


    /**
     *  this function taking a thread from the thread pool and send the
     *  maze to the server to solve it
     */
    @Override
    public void solveMaze() {
        threadPool.execute(() -> {
            CommunicateWithServer_SolveSearchProblem();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            setChanged();
            notifyObservers();
        });
    }






    /**
     * Handle the numpad presses
     * @param movement - movement key
     */
    @Override
    public void moveCharacter(KeyCode movement) {
        boolean playerMoved = false;//check if the player pressed on one of the controls
        switch (movement) {
            case NUMPAD8://up
                if(checkStep(characterPositionRow,characterPositionColumn-1)) {
                    characterPositionColumn--;
                    playerMoved = true;
                }
                break;
            case NUMPAD2://down
                if(checkStep(characterPositionRow,characterPositionColumn+1)) {
                    characterPositionColumn++;
                    playerMoved = true;
                }
                break;
            case NUMPAD6://right
                if(checkStep(characterPositionRow+1,characterPositionColumn)) {
                    characterPositionRow++;
                    playerMoved = true;
                }
                break;
            case NUMPAD4://left
                if(checkStep(characterPositionRow-1,characterPositionColumn)) {
                    characterPositionRow--;
                    playerMoved = true;
                }
                break;
            case NUMPAD9://diagonal - up right
                if(checkStep(characterPositionRow+1,characterPositionColumn-1)) {
                    characterPositionRow++;
                    characterPositionColumn--;
                    playerMoved = true;
                }
                break;
            case NUMPAD7://diagonal - up left
                if(checkStep(characterPositionRow-1,characterPositionColumn-1)) {
                    characterPositionRow--;
                    characterPositionColumn--;
                    playerMoved = true;
                }
                break;
            case NUMPAD3://diagonal - down right
                if(checkStep(characterPositionRow+1,characterPositionColumn+1)) {
                    characterPositionRow++;
                    characterPositionColumn++;
                    playerMoved = true;
                }
                break;
            case NUMPAD1://diagonal - down left
                if(checkStep(characterPositionRow-1,characterPositionColumn+1)) {
                    characterPositionRow--;
                    characterPositionColumn++;
                    playerMoved = true;
                }
                break;
        }
        if(playerMoved){
            setChanged();
            notifyObservers();
        }
    }


    /**
     * Check if the new position is valid
     * @param row - row that he wanna go
     * @param col - column that he wanna go
     * @return
     */
    private boolean checkStep(int row,int col) {
        return (row < this.maze.get_Row() && row >=0 &&
                col < this.maze.get_Column() && col >=0 &&
                this.maze.valueAt(row, col) == 0);
    }



    //-------------------------------//
    //---Communicatin with servers---//
    //-------------------------------//

    /**
     * this function open connection with the generation server
     * and send to him two values to the maze
     * @param width the rows
     * @param height the columns
     */
    private void CommunicateWithServer_MazeGenerating(int width, int height) {
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5400, new IClientStrategy() {
                @Override
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();

                        int[] mazeDimensions = new int[]{width, height};
                        toServer.writeObject(mazeDimensions); //send maze dimensions to server
                        toServer.flush();
                        byte[] compressedMaze = (byte[]) fromServer.readObject(); //read generated maze (compressed with MyCompressor) from server
                        InputStream is = new MyDecompressorInputStream(new ByteArrayInputStream(compressedMaze));
                        byte[] decompressedMaze = new byte[(width*height)+12]; //allocating byte[] for the decompressed maze
                        is.read(decompressedMaze); //Fill decompressedMaze with bytes

                        //creating new maze from the compressed maze
                        Maze maze = new Maze(decompressedMaze);

                        //setting the data
                        setMaze(maze);
                        characterPositionRow = maze.getStartPosition().getRowIndex();
                        characterPositionColumn = maze.getStartPosition().getColumnIndex();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            client.communicateWithServer();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }


    /**
     * this function open comunication with the solve server
     * and send to him the maze
     */
    private void CommunicateWithServer_SolveSearchProblem() {
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5401, new IClientStrategy() {
                @Override
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();

                        //send maze to server
                        toServer.writeObject(maze);
                        toServer.flush();

                        //getting the solution
                        Solution mazeSolution = (Solution) fromServer.readObject(); //read generated maze (compressed with MyCompressor) from server

                        //setting the solution
                        setSolution(mazeSolution);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            client.communicateWithServer();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }



    //-----------------------//
    //---Save & Load Mazes---//
    //-----------------------//
    public void loadMaze(File file){
        try{
            FileInputStream fin = new FileInputStream(file);
            ObjectInputStream oin = new ObjectInputStream(fin);
            Maze loadedMaze = (Maze) oin.readObject();
            if(loadedMaze != null) {
                maze = loadedMaze;
                characterPositionRow = maze.getStartPosition().getRowIndex();
                characterPositionColumn = maze.getStartPosition().getColumnIndex();
                setChanged();
                notifyObservers("Loaded_Maze");
            }
            fin.close();
            oin.close();
        }catch (IOException e){
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void saveCurrentMaze(File file){
        try {
            FileOutputStream fileWriter = new FileOutputStream(file);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileWriter);
            //Maze currentMaze = new Maze (maze.get_Row(), maze.get_Column());
            objectOutputStream.writeObject(maze);
            objectOutputStream.flush();
            objectOutputStream.close();
            fileWriter.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }




    //--------------//
    //----Getters---//
    //-------------//
    @Override
    public Maze getMaze() {
        return maze;
    }

    @Override
    public Solution getSolution() {
        return solution;
    }

    @Override
    public int getCharacterPositionRow() {
        return characterPositionRow;
    }

    @Override
    public int getCharacterPositionColumn() {
        return characterPositionColumn;
    }


    //------------//
    //---Setters--//
    //------------//
    private void setMaze(Maze maze){
        this.maze = maze;
    }

    private void setSolution(Solution solution){
        this.solution = solution;
    }
}
