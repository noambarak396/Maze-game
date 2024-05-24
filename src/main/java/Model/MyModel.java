package Model;
import Client.*;
import IO.*;
import Server.*;
import algorithms.mazeGenerators.*;
import algorithms.search.*;
import Client.IClientStrategy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Observable;
import java.util.Observer;

public class MyModel extends Observable implements IModel {

    Server mazeGeneratingServer;
    Server solveSearchProblemServer;
    private Maze Maze;
    private int[][] myMazeMap;
    private int indexRowPlayer;
    private int indexColumnPlayer;
    private Solution solution;
    private final Logger LOG = LogManager.getLogger();

    public MyModel() {
        mazeGeneratingServer= new Server(5400, 1000, new ServerStrategyGenerateMaze());
        solveSearchProblemServer = new Server(5401, 1000, new ServerStrategySolveSearchProblem());
    }

    @Override
    public int getIndexRowPlayer() {
        return indexRowPlayer;
    }

    @Override
    public int getIndexColumnPlayer() {
        return indexColumnPlayer;
    }

    @Override
    public void generateMaze(int rows, int cols) {
        try {
            Client client = new Client(InetAddress.getByName("127.0.0.1"), 5400, new IClientStrategy() {
                @Override
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        int[] mazeDimensions = new int[]{rows, cols};
                        toServer.writeObject(mazeDimensions); //send maze dimensions to server
                        toServer.flush();
                        byte[] compressedMaze = (byte[])fromServer.readObject(); //read generated maze (compressed withMyCompressor) from server
                        InputStream is = new MyDecompressorInputStream(new ByteArrayInputStream(compressedMaze));
                        byte[] decompressedMaze = new byte[rows*cols+10 /*CHANGE SIZE ACCORDING TO YOU MAZE SIZE*/]; //allocating byte[] for the decompressed maze -
                        is.read(decompressedMaze); //Fill decompressedMaze25 | P a g e with bytes
                        Maze = new Maze(decompressedMaze);
                        myMazeMap = Maze.getMaze_map();
                    } catch (Exception e) {
                        LOG.error("Generating maze: ",e);
                    }
                }
            });
            client.communicateWithServer();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            LOG.error("Exception while generating maze: ",e);
        }

        setChanged();
        notifyObservers("maze generated");
        // start position:
        movePlayer(0, 0);
        LOG.info("Maze has been generated");
    }

    @Override
    public void solveMaze() {
        //solve the maze
        solution = new Solution();
        try {
            Client client = new Client(InetAddress.getByName("127.0.0.1"), 5401, new IClientStrategy() {
                @Override
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        toServer.writeObject(Maze); //send maze to server
                        toServer.flush();
                        Solution mazeSolution = (Solution)fromServer.readObject(); //read generated maze (compressed with MyCompressor) from server%s", mazeSolution));
                        solution=mazeSolution;
                    } catch (Exception e) {
                        LOG.error("Exception while saving maze: ",e );
                    }
                }
            });
            client.communicateWithServer();
        } catch (UnknownHostException e) {
            LOG.error("Exception while saving maze: ", e);
        }
        setChanged();
        notifyObservers("maze solved");
        LOG.info("Maze has been solved");
    }

    @Override
    public void updatePlayerLocation(MovementDirection direction) {
        switch (direction) {
            case UP:
                if (indexRowPlayer > 0 && myMazeMap[indexRowPlayer - 1][indexColumnPlayer] != 1) {
                    movePlayer(indexRowPlayer - 1, indexColumnPlayer);
                }
                break;
            case LEFT:
                if (indexColumnPlayer > 0 && myMazeMap[indexRowPlayer][indexColumnPlayer - 1] != 1) {
                    movePlayer(indexRowPlayer, indexColumnPlayer - 1);
                }
                break;
            case DOWN:
                if (indexRowPlayer < myMazeMap.length - 1 && myMazeMap[indexRowPlayer + 1][indexColumnPlayer] != 1) {
                    movePlayer(indexRowPlayer + 1, indexColumnPlayer);
                }
                break;
            case RIGHT:
                if (indexColumnPlayer < myMazeMap[0].length - 1 && myMazeMap[indexRowPlayer][indexColumnPlayer + 1] != 1) {
                    movePlayer(indexRowPlayer, indexColumnPlayer + 1);
                }
                break;
            case DOWNRIGHT:
                if (indexRowPlayer < myMazeMap.length - 1 && indexColumnPlayer < myMazeMap[0].length - 1 && myMazeMap[indexRowPlayer + 1][indexColumnPlayer + 1] != 1) {
                    movePlayer(indexRowPlayer + 1, indexColumnPlayer + 1);
                }
                break;
            case UPRIGHT:
                if (indexColumnPlayer < myMazeMap[0].length - 1 && indexRowPlayer > 0 && myMazeMap[indexRowPlayer - 1][indexColumnPlayer + 1] != 1) {
                    movePlayer(indexRowPlayer - 1, indexColumnPlayer + 1);
                }
                break;
            case DOWNLEFT:
                if (indexColumnPlayer > 0 && indexRowPlayer < myMazeMap.length - 1 && myMazeMap[indexRowPlayer + 1][indexColumnPlayer - 1] != 1) {
                    movePlayer(indexRowPlayer + 1, indexColumnPlayer - 1);
                }
                break;
            case UPLEFT:
                if (indexColumnPlayer > 0 && indexRowPlayer > 0 && myMazeMap[indexRowPlayer - 1][indexColumnPlayer - 1] != 1) {
                    movePlayer(indexRowPlayer - 1, indexColumnPlayer - 1);
                }
                break;
        }
        LOG.debug("Player has been moved " + direction);
    }

    public void startServers() {
        this.solveSearchProblemServer.start();
        this.mazeGeneratingServer.start();

    }

    public void stopServers() {
        this.mazeGeneratingServer.stop();
        this.solveSearchProblemServer.stop();
    }

    public void movePlayer(int row, int col){
        this.indexRowPlayer = row;
        this.indexColumnPlayer = col;
        setChanged();
        notifyObservers("player moved");
        LOG.debug("Player in [ " + row +", "+ col +" ]");
    }


    @Override
    public void setMyMazeMap(Maze maze1){
        Maze = maze1;
        myMazeMap = maze1.getMaze_map();
        solution = null;
    }

    @Override
    public Maze getMyMazeMap() {
        return Maze;
    }

    @Override
    public Solution getSolution() {
        return solution;
    }

    @Override
    public void assignObserver(Observer o) {
        this.addObserver(o);
    }

}
