The Image Processor is a Java application designed to process images by averaging color values in square regions.
It provides both single-threaded and multi-threaded modes for image processing.

Running the Application:
1. Make sure JDK (Java Development Kit) is installed on your machine.
2. Open a terminal or command prompt.
3. Navigate to the directory containing the Java class file.
4. Run the application using the following commands:
    1. javac ImageProcessor.java
    2. java ImageProcessor <file_name> <square_size> <mode>
        file_name: the name of the image. If it is in the different directory, path can be written too.
        square_size: it is the size of the square, and it should be an integer.
        mode: M stands for Multi_Thread while S stands for single thread.
        Example: java ImageProcessor example.jpg 10 M

Result:

Single-Threaded Mode:
Processes the image in a sequential, left-to-right, top-to-bottom manner.
Updates the GUI progressively, displaying the averaged squares.

Multi-Threaded Mode:
Utilizes multiple threads to process the image concurrently.
Divides the image into vertical portions for parallel processing.

At the end of each process, when the window is closed, result will be saved in a result.jpg file.
    

