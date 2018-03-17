package io.zipcoder;

import java.util.*;

public class ItemParser {
    private int errorCount = 0;

    private Map<String, List<Item>> itemData = new HashMap<>();


    public ArrayList<String> parseRawDataIntoStringArray(String rawData){
        String stringPattern = "##";
        ArrayList<String> response = splitStringWithRegexPattern(stringPattern , rawData);
        return response;
    }

    public Item parseStringIntoItem(String rawItem) throws ItemParseException{
        List<String> parsedRawDataIntoStringArray = this.parseRawDataIntoStringArray(rawItem);
        List<List<String>> keyValuePairs = new ArrayList<>();

        for (int i = 0; i < parsedRawDataIntoStringArray.size(); i++) {
            keyValuePairs.add(i,this.findKeyValuePairsInRawItemData(parsedRawDataIntoStringArray.get(i)));
        }

        for (int i = 0; i < keyValuePairs.size(); i++) {
            try {
                Item item = new Item(keyValuePairs.get(i).get(0).split(":")[1],
                        Double.parseDouble(keyValuePairs.get(i).get(1).split(":")[1]),
                        keyValuePairs.get(i).get(2).split(":")[1],
                        keyValuePairs.get(i).get(3).split(":")[1]);

                return this.fixSpelling(item);
            } catch (ArrayIndexOutOfBoundsException e) {
                errorCount++;
                continue;
            }
        }


        throw(new ItemParseException());
    }

    public List<Item> parseStringIntoItemArray(String rawItem) throws ItemParseException{
        List<Item> items = new ArrayList<>();
        List<String> parsedRawDataIntoStringArray = this.parseRawDataIntoStringArray(rawItem);
        List<List<String>> keyValuePairs = new ArrayList<>();

        for (int i = 0; i < parsedRawDataIntoStringArray.size(); i++) {
            keyValuePairs.add(i,this.findKeyValuePairsInRawItemData(parsedRawDataIntoStringArray.get(i)));
        }

        for (int i = 0; i < keyValuePairs.size(); i++) {
            try {
                Item item = new Item(keyValuePairs.get(i).get(0).split(":")[1],
                        Double.parseDouble(keyValuePairs.get(i).get(1).split(":")[1]),
                        keyValuePairs.get(i).get(2).split(":")[1],
                        keyValuePairs.get(i).get(3).split(":")[1]);

                items.add(this.fixSpelling(item));

                if(!itemData.containsKey(item.getName())) {
                    List<Item> newItemList = new ArrayList<>();
                    newItemList.add(item);
                    itemData.put(item.getName(), newItemList);
                } else {
                    for(String key: itemData.keySet()) {
                        if(item.getName().equals(key)) {
                            itemData.get(key).add(item);
                        }
                    }
                }

            } catch (ArrayIndexOutOfBoundsException e) {
                errorCount++;
                continue;
                //throw(new ItemParseException());


            }
        }

        return items;

    }

    public ArrayList<String> findKeyValuePairsInRawItemData(String rawItem){
        String stringPattern = "[^a-zA-Z0-9/.:]";
        ArrayList<String> response = splitStringWithRegexPattern(stringPattern , rawItem);
        return response;
    }

    private ArrayList<String> splitStringWithRegexPattern(String stringPattern, String inputString){
        return new ArrayList<>(Arrays.asList(inputString.split(stringPattern)));
    }


    public Item fixSpelling(Item item) {

        if(item.getName().matches("(m|M)(i|I)(l|L)(k|K)")) {
            item.setName("milk");
        }

        else if(item.getName().matches("(b|B)(r|R)(e|E)(a|A)(d|D)")) {
            item.setName("bread");
        }

        else if(item.getName().matches("((c|C)(((o|O)0)|((0)(o|O))|((o|O)(o|O)))(k|K)(i|I)(e|E)(s|S))")) {
            item.setName("cookies");
        }

        else if(item.getName().matches("(a|A)(p|P)(p|P)(l|L)(e|E)(s|S)")) {
            item.setName("apples");
        }

        if(item.getType().matches("(f|F)(o|O)(o|O)(d|D)")) {
            item.setType("food");
        }

        return item;
    }

    public String formatData() {
        StringBuilder printKey = new StringBuilder();

        Map<Double, Integer> pricesSeen = new HashMap<>();
        Map<String, Integer> typesSeen = new HashMap<>();
        Map<String, Integer> expirationsSeen = new HashMap<>();

        Iterator itemDataIterator = itemData.entrySet().iterator();
        while (itemDataIterator.hasNext()) {
            Map.Entry pair = (Map.Entry) itemDataIterator.next();
            printKey.append("\n\n").append(pair.getKey()).append("\t\t\t\t\t\t Occurrences:  ").append(((List<Item>) pair.getValue()).size()).append("\n=============================================");

            for (Item currentItem : itemData.get(pair.getKey())) {
                // Price Map
                Integer numTimesPriceSeen = 0;
                if (pricesSeen.containsKey(currentItem.getPrice())) {
                    numTimesPriceSeen = pricesSeen.get(currentItem.getPrice());
                }
                numTimesPriceSeen++;
                pricesSeen.put(currentItem.getPrice(), numTimesPriceSeen);

                // Type Map
                Integer numTimesTypeSeen = 0;
                if(typesSeen.containsKey(currentItem.getType())) {
                    numTimesTypeSeen = typesSeen.get(currentItem.getType());
                }
                numTimesTypeSeen++;
                typesSeen.put(currentItem.getType(), numTimesTypeSeen);

                // ExpirationMap
                Integer numTimesExpirationSeen = 0;
                if(expirationsSeen.containsKey(currentItem.getExpiration())) {
                    numTimesExpirationSeen = expirationsSeen.get(currentItem.getExpiration());
                }
                numTimesExpirationSeen++;
                expirationsSeen.put(currentItem.getExpiration(), numTimesExpirationSeen);
            }

            Iterator priceMapIterator = pricesSeen.entrySet().iterator();
            while(priceMapIterator.hasNext()) {
                Map.Entry priceMapPair = (Map.Entry) priceMapIterator.next();
                printKey.append(String.format("%n%s%13s%7s%15s%3d","Price:","$"+priceMapPair.getKey(),"|", "Occurrences:", priceMapPair.getValue())).append("\n---------------------------------------------");
            }

            Iterator typeMapIterator = typesSeen.entrySet().iterator();
            while(typeMapIterator.hasNext()) {
                Map.Entry typeMapPair = (Map.Entry) typeMapIterator.next();
                printKey.append(String.format("%n%s%13s%8s%15s%3d","Type:", typeMapPair.getKey(),"|", "Occurrences:", typeMapPair.getValue())).append("\n---------------------------------------------");
            }

            Iterator expirationIterator = expirationsSeen.entrySet().iterator();
            while(expirationIterator.hasNext()) {
                Map.Entry expirationMapPair = (Map.Entry) expirationIterator.next();
                printKey.append("\nExpiration: ").append(expirationMapPair.getKey()).append("\t |   Occurrences:  ").append(expirationMapPair.getValue()).append("\n---------------------------------------------");
            }

            pricesSeen.clear();
            typesSeen.clear();
            expirationsSeen.clear();
        }
        printKey.append("\n\nNumber of Errors: " + errorCount);
        return printKey.toString();
    }
}
