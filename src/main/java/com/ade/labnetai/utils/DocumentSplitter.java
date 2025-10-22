package com.ade.labnetai.utils;

import java.util.ArrayList;
import java.util.List;

public class DocumentSplitter {
    private final int maxTokenSize;
    private final int overlapTokenSize;
    private final String[] separators = {"\n\n", "\n", ".", " ", ""};

    public DocumentSplitter(int maxTokenSize, int overlapTokenSize) {
        this.maxTokenSize = maxTokenSize;
        this.overlapTokenSize = overlapTokenSize;
    }

    public List<String> split(String text) {
        // Normalize whitespace
        text = text.replaceAll("\\s+", " ").trim();

        // Start recursive splitting
        return splitRecursive(text);
    }

    private List<String> splitRecursive(String text) {
        List<String> chunks = new ArrayList<>();

        if (text.length() <= maxTokenSize) {
            if (!text.trim().isEmpty()) {
                chunks.add(text.trim());
            }
            return chunks;
        }

        for (String separator : separators) {
            if (!separator.isEmpty() && text.contains(separator)) {
                String[] parts = text.split(java.util.regex.Pattern.quote(separator));
                List<String> currentChunk = new ArrayList<>();
                int currentLength = 0;

                for (int i = 0; i < parts.length; i++) {
                    String part = parts[i].trim();
                    if (part.isEmpty()) continue;

                    // Add back separator unless it's the last part
                    if (i < parts.length - 1) {
                        part += separator;
                    }

                    if (currentLength + part.length() > maxTokenSize) {
                        // Combine into chunk
                        String chunk = String.join("", currentChunk).trim();
                        if (!chunk.isEmpty()) {
                            chunks.add(chunk);
                        }

                        // Add overlap (without cutting words)
                        String overlap = getOverlap(chunk);
                        currentChunk = new ArrayList<>();
                        if (!overlap.isEmpty()) {
                            currentChunk.add(overlap);
                            currentLength = overlap.length();
                        } else {
                            currentLength = 0;
                        }
                    }

                    currentChunk.add(part);
                    currentLength += part.length();
                }

                // Add final chunk
                String finalChunk = String.join("", currentChunk).trim();
                if (!finalChunk.isEmpty()) {
                    chunks.add(finalChunk);
                }

                return chunks;
            }
        }

        // Fallback: Hard split with word boundary protection
        int start = 0;
        while (start < text.length()) {
            int end = Math.min(start + maxTokenSize, text.length());

            // If end is mid-word, backtrack to previous space
            if (end < text.length() && text.charAt(end) != ' ') {
                int backtrack = text.lastIndexOf(' ', end);
                if (backtrack > start) {
                    end = backtrack;
                }
            }

            String chunk = text.substring(start, end).trim();
            if (!chunk.isEmpty()) {
                chunks.add(chunk);
            }

            // Prepare next start index with overlap
            start = end - overlapTokenSize;
            if (start < 0) start = 0;
        }

        return chunks;
    }

    private String getOverlap(String chunk) {
        if (chunk.length() <= overlapTokenSize) {
            return chunk;
        }

        // Avoid cutting a word mid-way in the overlap section
        String overlap = chunk.substring(chunk.length() - overlapTokenSize);
        int firstSpace = overlap.indexOf(' ');
        if (firstSpace > 0 && firstSpace < overlap.length() - 1) {
            return overlap.substring(firstSpace + 1); // skip partial word
        }

        return overlap;
    }
}
