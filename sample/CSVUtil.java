package com.amararaja.services.masters.utility.utils;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class CSVUtil {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CSVUtil.class);

	public List<Map<String, String>> readCsv(final String filePath) {
		try (final Reader reader = Files.newBufferedReader(Paths.get(filePath));
				final CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);) {
			final Map<Integer, String> headerMap = new HashMap<>();
			final List<Map<String, String>> records = new ArrayList<>();
			int index = 0;
			for (CSVRecord csvRecord : csvParser) {
 				final Iterator<String> iterator = csvRecord.iterator();
				if (index == 0) {
					// Reading header cell names
					while (iterator.hasNext()) {
						headerMap.put(index, iterator.next());
						index++;
					}
				} else {
					index = 0;
					// Reading header cell values
					final Map<String, String> valuesMap = new HashMap<>();
					while (iterator.hasNext()) {
						valuesMap.put(headerMap.get(index), iterator.next());
						index++;
					}
					records.add(valuesMap);
				}

			}

			return records;

		} catch (Exception e) {
			LOGGER.error("Exception occured while parisng  csv:", e);
		}
		return null;
	}

	public static void csvWriter(List<Map<String, String>> listOfMap, Writer writer) throws IOException {
		List<String> headers = new ArrayList<>();
		for (String col : listOfMap.get(0).keySet()) {
			headers.add(col);
		}
		CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader((String[]) headers.toArray()));
		while (listOfMap.iterator().hasNext()) {
			List<String> values = listOfMap.stream().map(record -> record.values())
					.flatMap(strings -> strings.stream()).collect(Collectors.toList());
			csvPrinter.printRecords(values);
		}
		csvPrinter.flush();
	}
}
