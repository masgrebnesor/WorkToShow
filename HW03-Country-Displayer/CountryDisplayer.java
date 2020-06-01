import java.io.File; 
import java.io.FileNotFoundException;  
import java.util.Scanner; 
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;

public class CountryDisplayer {
  ArrayList<Country> countriesData = new ArrayList<Country>();
  ArrayList<Country> sortedCountriesData = new ArrayList<Country>();
    public CountryDisplayer(String filepath) {
        // Read the country file and load the countries into this instance of CountryDisplayer.
      try {
        File file = new File(filepath);
        Scanner reader = new Scanner(file);
        String data = reader.nextLine();
        while (reader.hasNextLine()) 
        {
          data = reader.nextLine();
          String[] countrydata = data.split(",");
          Country newCountry = new Country(countrydata[0],Float.parseFloat(countrydata[1]), Float.parseFloat(countrydata[2]), Float.parseFloat(countrydata[3]), Float.parseFloat(countrydata[4]), Float.parseFloat(countrydata[5]), Float.parseFloat(countrydata[6]), Float.parseFloat(countrydata[7]));
          countriesData.add(newCountry);
        }
        reader.close();
      } catch (FileNotFoundException e) 
      {
        System.out.println("An error occurred.");
      }
    }

    // sorts the country list by a given category.
    public void sortCountryList(String criteria, String direction) {
        // ...
        ArrayList<Country> SortedCountries = new ArrayList<Country>();
        for (Country item : countriesData) {
          int index = 0;
          int added = 0;
          if (direction.equals("greatestToLeast"))
          {
            for (Country sorted: SortedCountries)
            {
              if (item.compareTo(sorted, criteria))
              {
                SortedCountries.add(index, item);
                added = 1;
                break;
              }
            index++;
            }
            if (added == 0)
            {
              SortedCountries.add(item);
            }
          }
          else
          {
            for (Country sorted: SortedCountries)
            {
              if (!item.compareTo(sorted, criteria))
              {
                SortedCountries.add(index, item);
                added = 1;
                break;
              }
            index++;
            }
            if (added == 0)
            {
              SortedCountries.add(item);
            }
          }

        }
        sortedCountriesData = SortedCountries;
    }
    // displayes sorted countries in text form
    public void displayTextCountries() {
        // ...
        for (Country item: sortedCountriesData)
        {
          item.displayString();
        }
    }
    
    //displays graphed information of sorted countries with the sorted and other criteria.
    public void displayCountryGraph(String criteriaSorted, String indicatorOther) {
        // ...
        BarChart barchart = new BarChart("Top Ten " + criteriaSorted, "Country", "Title");
        int items = -1;
        if (sortedCountriesData.size() > 10)
        {
          items = 10;
        }
        else
        {
          items = sortedCountriesData.size();
        }
        if (items > 0)
        {
        for (int i = 0; i < items; i++)
        {
          barchart.addValue(sortedCountriesData.get(i).name, sortedCountriesData.get(i).sortingValue(criteriaSorted), criteriaSorted);
          barchart.addValue(sortedCountriesData.get(i).name, sortedCountriesData.get(i).sortingValue(indicatorOther), indicatorOther);
        }
        barchart.displayChart();
        }
    }
     
    public static void main(String[] args) {
// javac -classpath .:jfreechart-1.5.0.jar *.java
 //java -classpath .:jfreechart-1.5.0.jar CountryDisplayer CountryDataset.csv PopulationGrowth greatestToLeast UrbanPopulationGrowth 

        // If there's no command-line argument, print a usage statement 
        // and exit. Otherwise, use args[0] as the input file path.
        if (args.length < 2 || (!args[2].equals("greatestToLeast") && !args[2].equals("leastToGreatest"))) {
           System.out.println("The program needs several arguments to run. You can input either 3 arguments, or 4. For 3: a csv, a statistic to sort by, and a direction to sort. This will lead to a text display. With 4, you get a graph: a csv, a statistic to sort by, and a direction to sort, an additional piece of information.");
        }
        else
        {
          CountryDisplayer display = new CountryDisplayer(args[0]);
          display.sortCountryList(args[1],args[2]);
          if (args.length > 3)
          {
            if((!args[3].equals("CO2Emissions") && !args[3].equals("AccessToElectricity") && !args[3].equals("RenewableEnergy") && !args[3].equals("ProtectedAreas") && !args[3].equals("PopulationGrowth") && !args[3].equals("PopulationTotal") && !args[3].equals("UrbanPopulationGrowth")))
            {
              System.out.println("The program needs several arguments to run. You can input either 3 arguments, or 4. For 3: a csv, a statistic to sort by, and a direction to sort. This will lead to a text display. With 4, you get a graph: a csv, a statistic to sort by, and a direction to sort, an additional piece of information.");
            }
            else
            {
            display.displayCountryGraph(args[1], args[3]);
            }
          }
          else
          {
            display.displayTextCountries();
          }
        }
        //Construct a CountryDisplayer and call the methods you defined above to load, process, and display the countries
    }
}