package org.kreyzon.stripe.service;

import java.io.*;
import java.util.concurrent.atomic.AtomicInteger;

public class SequenceGenerator {
    private static final String SEQUENCE_FILE = "sequence.txt1234567";
    private static final AtomicInteger sequence = new AtomicInteger(loadSequence());

    public static int getNextSequenceNumber() {
        int nextNumber = sequence.incrementAndGet(); // Increment and get the next number
        saveSequence(nextNumber); // Save the updated sequence to the file
        return nextNumber;
    }

    private static int loadSequence() {
        File file = new File(SEQUENCE_FILE);
        if (!file.exists()) {
            // File does not exist, so we start the sequence at 0 (because it will be incremented to 1)
            return 0;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            return (line != null) ? Integer.parseInt(line) : 0; // Start from 0 if the file is empty
        } catch (IOException e) {
            e.printStackTrace();
            return 0; // Start from 0 if there's an error reading the file
        }
    }

    private static void saveSequence(int sequenceNumber) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SEQUENCE_FILE))) {
            writer.write(Integer.toString(sequenceNumber)); // Write the sequence number to the file
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
