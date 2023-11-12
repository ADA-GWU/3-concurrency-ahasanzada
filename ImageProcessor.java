import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ImageProcessor extends JFrame {
    private BufferedImage image;
    private BufferedImage resultImage;
    private int squareSize;
    private JLabel imageLabel;

    public ImageProcessor(String fileName, int squareSize) {
        try {
            this.image = ImageIO.read(new File(fileName));
            this.resultImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
            // Initialize resultImage
            Graphics g = resultImage.getGraphics();
            g.drawImage(image, 0, 0, null);
            g.dispose();

            this.squareSize = squareSize;

            setTitle("Image Processor");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLayout(new BorderLayout());
            imageLabel = new JLabel(new ImageIcon(resultImage));
            add(imageLabel, BorderLayout.CENTER);
            pack();
            setLocationRelativeTo(null);
            setVisible(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void processSingleThreaded() {
        for (int y = 0; y < image.getHeight(); y += squareSize) {
            for (int x = 0; x < image.getWidth(); x += squareSize) {
                processSquare(x, y);
                updateImageLabel();
                sleep();
            }
        }
    }

    public void processMultiThreaded(int numThreads) {
        Thread[] threads = new Thread[numThreads];
        int squaresPerThread = image.getHeight() / (squareSize * numThreads);

        for (int i = 0; i < numThreads; i++) {
            final int threadIndex = i;
            threads[i] = new Thread(() -> {
                for (int y = threadIndex * squaresPerThread * squareSize; y < (threadIndex + 1) * squaresPerThread * squareSize && y < image.getHeight(); y += squareSize) {
                    for (int x = 0; x < image.getWidth(); x += squareSize) {
                        processSquare(x, y);
                        updateImageLabel();
                        sleep();
                    }
                }
            });
            threads[i].start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateImageLabel() {
        SwingUtilities.invokeLater(() -> {
            imageLabel.setIcon(new ImageIcon(resultImage));
            imageLabel.repaint();
        });
    }

    private void sleep() {
        try {
            Thread.sleep(10); // Delay
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void processSquare(int x, int y) {
        int[] rgbValues = calculateAverageColor(x, y);
        fillSquare(x, y, rgbValues);
    }

    private int[] calculateAverageColor(int startX, int startY) {
        int[] rgbSum = new int[3];

        for (int x = startX; x < startX + squareSize && x < image.getWidth(); x++) {
            for (int y = startY; y < startY + squareSize && y < image.getHeight(); y++) {
                int rgb = image.getRGB(x, y);
                rgbSum[0] += (rgb >> 16) & 0xFF;
                rgbSum[1] += (rgb >> 8) & 0xFF;
                rgbSum[2] += rgb & 0xFF;
            }
        }

        int numPixels = squareSize * squareSize;
        return new int[]{
                rgbSum[0] / numPixels,
                rgbSum[1] / numPixels,
                rgbSum[2] / numPixels
        };
    }

    private void fillSquare(int startX, int startY, int[] rgbValues) {
        for (int x = startX; x < startX + squareSize && x < image.getWidth(); x++) {
            for (int y = startY; y < startY + squareSize && y < image.getHeight(); y++) {
                resultImage.setRGB(x, y, (rgbValues[0] << 16) | (rgbValues[1] << 8) | rgbValues[2]);
            }
        }
    }

    private void saveResultImage() {
        try {
            File output = new File("result.jpg");
            ImageIO.write(resultImage, "jpg", output);
            System.out.println("Result image saved as result.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: java ImageProcessor <file_name> <square_size> <mode>");
            System.exit(1);
        }

        String fileName = args[0];
        int squareSize = Integer.parseInt(args[1]);
        String mode = args[2].toUpperCase();

        if (!mode.equals("S") && !mode.equals("M")) {
            System.out.println("Invalid mode. Use 'S' for single-threaded or 'M' for multi-threaded.");
            System.exit(1);
        }

        ImageProcessor processor = new ImageProcessor(fileName, squareSize);

        if (mode.equals("S")) {
            processor.processSingleThreaded();
        } else {
            int numThreads = Runtime.getRuntime().availableProcessors();
            processor.processMultiThreaded(numThreads);
        }

        processor.saveResultImage();
    }
}
