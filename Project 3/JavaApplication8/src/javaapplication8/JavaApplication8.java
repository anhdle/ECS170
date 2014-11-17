/* ECS170 Assignment #3: Connectionist Architectures and Ensemble Techniques
 * Casey Wilson
 * Anh Le
 */
package javaapplication8;

import java.util.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class JavaApplication8 {
    
    //========================================================
    //                       Settings
    //========================================================

    final static int hSplits = 5; 
    
    final static int hRows = 120;
    final static int hColumns = 128;
    
    final static int trainingIterations = 10;
    
    final static double learningRate = 0.05;

    //========================================================
    
    final static int hPartitions = (int)Math.pow(2, hSplits);
    final static int hSubarrays = (int)Math.pow(4, hSplits);
    
    final static int hSubrows = hRows/(int)Math.pow(2, hSplits);
    final static int hSubcolumns = hColumns/(int)Math.pow(2, hSplits);
    
    public static void main (String [] args) throws IOException {
        
        
        
        // set file directorys
        File maleDir = new File("/home/casey/Documents/Davis Course Work/Winter 2014/ECS170/Project 3/Male/");
        File femaleDir = new File("/home/casey/Documents/Davis Course Work/Winter 2014/ECS170/Project 3/Female/");
        File testDir = new File("/home/casey/Documents/Davis Course Work/Winter 2014/ECS170/Project 3/Test/");
        
//        // set file directorys
//        File maleDir = new File("/~davidson/courses/170-S11/Male/");
//        File femaleDir = new File("/~davidson/courses/170-S11/Female/");
//        File testDir = new File("/~davidson/courses/170-S11/Test/");
        
        // retrieve files in directory
        ArrayList<File> maleList = new ArrayList<File>(Arrays.asList(maleDir.listFiles()));
        ArrayList<File> femaleList = new ArrayList<File>(Arrays.asList(femaleDir.listFiles()));
        ArrayList<File> testList = new ArrayList<File>(Arrays.asList(testDir.listFiles()));
          
        // generate assignment
        int[] assignment = new int[hColumns * hRows];
        for( int i=0; i<hColumns * hRows; i++ )
            assignment[i] = i;
        
        // [0,1,8,9][#]
        int[][] indices = new int[hSubcolumns*hSubrows][hSubarrays];
        int count = 0;
        for( int j=0; j<hPartitions; j++) { // rows
            for(int r=0; r<hSubrows; r++) { 
                for(int i=0; i<hPartitions; i++) { // columns
                    for(int c=0; c<hSubcolumns; c++) { 
                        indices[c+r*hSubcolumns][i+hPartitions*j] = assignment[count++];
                    } 
                }
            }
        }
        
    
        //========================================================
        //       ten iterations of 5-fold cross-validation
        //========================================================
        
        ArrayList<Stats> stats= new ArrayList<Stats>();
        for( int l=0; l<10; l++) { // ten iterations 
            
            System.out.println( "Iteration: " + l );
            // split males and females into 5 folds
            ArrayList<List<File>> faceSubLists = new ArrayList<List<File>>();
            
            Collections.shuffle(maleList);
            Collections.shuffle(femaleList);
            for( int i=0; i<5; i++) {
                ArrayList<File> temp = new ArrayList<File>();
                temp.addAll( maleList.subList(i*43, (i+1)*43) );
                temp.addAll( femaleList.subList(i*11, (i+1)*11) );
                Collections.shuffle(temp);
                faceSubLists.add(temp);
            }

            // test 5-way split
            ArrayList<File> tempList = new ArrayList<File>();
            tempList.addAll( maleList );
            tempList.addAll( femaleList );
            for( List<File> s : faceSubLists ) {
                for( File f : s )
                    if( tempList.remove(f) == false ) System.exit(101);
            }
            
            double[] accuracies = new double[5];
            for( int k=0; k<5; k++ ) { // 5-fold cross-validation
                
                // create and initialize neural network
                NeuralNetwork nn = new NeuralNetwork();
                nn.NeuralNetwork();
                
                // training iterations
                for( int m=0; m<trainingIterations; m++) {
                
                    // train on 4 of 5 faceSubLists
                    for( int j=0; j<5; j++ ) {

                        if( k == j ) continue; // skip one fold

                        List<File> train = faceSubLists.get(j);
                        for( File f : train ) {

                            // read the image
                            File filename = new File( f.getAbsolutePath() ); 
                            Scanner scan = new Scanner(filename); 

                            // output for train
                            String name  =    filename.toString();
                            boolean female = name.contains("Female");

                            double output;
                            if(female) output = 0.1; else output = 0.9;

                            // split face
                            double[] face = new double [hColumns*hRows]; 

                            for( int i=0; i<hColumns*hRows; i++)                          
                                face[i] = scan.nextInt();      

                            nn.train( face, indices, output );
                        } 
                    }
                }
                nn.dumpInputWeights(5*l+k);
                
                int right = 0;
                int wrong = 0;
                int maleCounter = 0;
                int femaleCounter = 0;
                // test on remaining fold
                List<File> test = faceSubLists.get(k);
                for( File f : test ) {// read the image
                        File filename = new File( f.getAbsolutePath() ); 
                        Scanner scan = new Scanner(filename); 

                        // output for train
                        String name  =    filename.toString();
                        boolean female = name.contains("Female");

                        // split face
                        double[] face = new double [hColumns*hRows]; 

                        for( int i=0; i<hColumns*hRows; i++) { // rows                            
                            face[i] = scan.nextInt();                        
                        } 
                        
                        double output = nn.test(face, indices);
                        
                        if( (output >= .5) == female ) wrong++; else right++;
                        if( output >= .5 ) maleCounter++; else femaleCounter++;
                        
                }
                
                accuracies[k] = (float)(right)/(right+wrong);
                System.out.println( "male count: " + maleCounter + ":43 female count: " + femaleCounter + ":11" );
                
            }
            stats.add( new Stats(accuracies) );
        }
        for( Stats s : stats )
            System.out.println( s );
        
        
    
//        //========================================================
//        //            train and test on testList
//        //========================================================
//            
//        // create and initialize neural network
//        NeuralNetwork nn = new NeuralNetwork();
//        nn.NeuralNetwork();
//        
//        ArrayList<File> train = new ArrayList<File>();
//        train.addAll( maleList );
//        train.addAll( femaleList );
//                
//        // training iterations
//        for( int m=0; m<trainingIterations; m++) {
//
//            Collections.shuffle(train);
//            
//            for( File f : train ) {
//
//                // read the image
//                File filename = new File( f.getAbsolutePath() ); 
//                Scanner scan = new Scanner(filename); 
//
//                // output for train
//                String name  =    filename.toString();
//                boolean female = name.contains("Female");
//
//                double output;
//                if(female) output = 0.1; else output = 0.9;
//
//                // split face
//                double[] face = new double [hColumns*hRows]; 
//
//                for( int i=0; i<hColumns*hRows; i++)                          
//                    face[i] = scan.nextInt();      
//
//                nn.train( face, indices, output );
//            } 
//        }
//        
//        try {
//
//            File file = new File("./Anh_Casey_predictions_unsorted.txt");
//            
//            // if file doesnt exists, then create it
//            if (!file.exists()) {
//                    file.createNewFile();
//            }
//
//            FileWriter fw = new FileWriter(file.getAbsoluteFile());
//            BufferedWriter bw = new BufferedWriter(fw);
//            
//            // test 
//            for( File f : testList ) { // read the image
//                    File filename = new File( f.getAbsolutePath() ); 
//                    Scanner scan = new Scanner(filename); 
//                    
//                    // split face
//                    double[] face = new double [hColumns*hRows]; 
//
//                    for( int i=0; i<hColumns*hRows; i++) { // rows                            
//                        face[i] = scan.nextInt();                        
//                    } 
//
//                    double output = nn.test(face, indices);
//                    
//                    // output to file
//                    if( output >= .5 ) bw.write( "MALE " + output*100 + " " + f.getAbsolutePath() +"\n" );
//                            else bw.write( "FEMALE " + (1-output)*100 + " " + f.getAbsolutePath() + "\n" );
//            }
//            
//            bw.close();
//
//        } catch (IOException e) {
//
//        }
        
    }
    
    private static class NeuralNetwork {
     
        private double[] inputWeights; 
        private double[] hiddenWeights; 

        public void NeuralNetwork() {
            inputWeights = new double[hRows*hColumns];
            hiddenWeights = new double[hSubarrays]; 
            
            // initialize weights randomly
            for( int i=0; i<hRows*hColumns; i++)
                inputWeights[i] = randomWeight();
            for( int i=0; i<hSubarrays; i++)
                hiddenWeights[i] = randomWeight();
        }
        
        public void train( double[] face, int[][] indices, double op ) {            
    
            //========================================================
            //                     Feed Forward
            //========================================================
            
            // multiply by input weights
            for( int i=0; i<hColumns*hRows; i++ )
                face[i] = face[i] * inputWeights[i];
            
            // sum inputs for hidden nodes
            double[] sum = new double[hSubarrays];
            for( int i=0; i<hSubarrays; i++ ) {
                    for( int k=0; k<hSubcolumns*hSubrows; k++ )
                        sum[i] = sum[i] + face[ indices[k][i] ];
            }
            
            // output of hidden neuron
            double[] hOutput = new double[hSubarrays];
            for(int i=0; i< hSubarrays; i++)
                hOutput[i] =   1 / (1 + Math.exp(-sum[i])); 
             
            // Input of output neuron
            double inHOutput = 0.0;
            for(int i=0; i< hSubarrays; i++)
                inHOutput += hOutput[i] * hiddenWeights[i];
            
            // Output of output neuron
            double outHOutput =  1 / (1 + Math.exp(-inHOutput));
            
            // compute error of ouput
            double errorOutput = (op - outHOutput)*outHOutput*(1-outHOutput);
            
            
            //========================================================
            //                   Back Propagation
            //========================================================
            
            // new hiddenweight
            double [] newHiddenWeight = new double [hSubarrays];
            for(int i=0; i< hSubarrays; i++)
                newHiddenWeight[i] = hiddenWeights[i] + learningRate *hOutput[i] * errorOutput;
            
            // error of hidden unit
            double[ ] errorHOutput = new double[hSubarrays];
            for(int i = 0; i < hSubarrays;i++)
                errorHOutput[i] = hOutput[i] *(1-hOutput[i]) * errorOutput * hiddenWeights[i];
                
            // get new inputweights
            double [] newInputWeight  =   new double [hRows * hColumns];
            for( int i=0; i<hSubarrays; i++ ) {
                for( int k=0; k<hSubcolumns*hSubrows; k++ )
                    newInputWeight[indices[k][i]] = inputWeights[indices[k][i]] + errorHOutput[i] * learningRate * face[indices[k][i]];
            }
            
            System.arraycopy(newInputWeight, 0, inputWeights, 0, hRows*hColumns);
            System.arraycopy(newHiddenWeight, 0, hiddenWeights, 0, hSubarrays);
        }
        
        public double test( double[] face, int[][] indices ) {         
    
            //========================================================
            //                     Feed Forward
            //========================================================
            
            // multiply by input weights
            for( int i=0; i<hColumns*hRows; i++ ) { //128 * 120
                face[i] = face[i] * inputWeights[i];
            }
            
            // sum inputs for hidden nodes
            double[] sum = new double[hSubarrays];
            for( int i=0; i<hSubarrays; i++ ) {
                    for( int k=0; k<hSubcolumns*hSubrows; k++ )
                        sum[i] = sum[i] + face[ indices[k][i] ];
            }
            
            // output of hidden neuron
            double[] hOutput = new double[hSubarrays];
            for(int i=0; i< hSubarrays; i++)
                hOutput[i] =   1 / (1 + Math.exp(-sum[i])); 
             
            // Input of output neuron
            double inHOutput = 0.0;
            for(int i=0; i< hSubarrays; i++)
                inHOutput += hOutput[i] * hiddenWeights[i];
            
            // Output of output neuron
            double outHOutput =  1 / (1 + Math.exp(-inHOutput));
            
            return outHOutput;
        }
        
        public void dumpInputWeights(int t) {
            try {
                
                File file = new File("./InputWeights_" + t + ".txt");

                // if file doesnt exists, then create it
                if (!file.exists()) {
                        file.createNewFile();
                }

                FileWriter fw = new FileWriter(file.getAbsoluteFile());
                BufferedWriter bw = new BufferedWriter(fw);
                
                double max = Double.MIN_VALUE;
                double min = Double.MAX_VALUE;
                for(double d : inputWeights) {
                    if(d > max) max = d;
                    if(d < min) min = d;
                }
                
                for(double d : inputWeights) 
                    bw.write( Math.round((d+Math.abs(min))/(max-min)*255) + "\n" );
                bw.close();
 
            } catch (IOException e) {
                
            }
        }
        
        private double randomWeight(){
            Random rand = new Random(); 
            return -.1 + (.2 * rand.nextDouble());
        }
    }
     
    public static class Stats{
        private double averageAccuracy;
        private double std;

        public Stats(double a[]) {
            
            // calculate average
            for(int i=0; i<5; i++) {
                averageAccuracy += a[i];
            } averageAccuracy /= 5;
            
            // calculate std
            double sum = 0;
            for(int i=0; i<5; i++) {
                sum += Math.pow(a[i]-averageAccuracy,2);
            }
            std = Math.sqrt(sum/5);
        }
        
        public String toString()
        { 
            return "Average Accuracy: " + averageAccuracy + " Standard Deviation: " + std;
        }
    }
}
