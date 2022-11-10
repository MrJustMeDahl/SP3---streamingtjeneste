import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import static java.lang.Thread.sleep;

public class MainMenu {

    private ArrayList<User> allUsers;
    private ArrayList<AMedia> allMedia;
    private ArrayList<AMedia> allMovies;
    private ArrayList<AMedia> allSeries;
    private ArrayList<String> allcategories = FileHandling.readFromCategoryFile("Data/Categories.txt");
    private Random rnd = new Random();
    private Scanner scanner = new Scanner(System.in);

    public MainMenu(ArrayList<AMedia> allMedia, ArrayList<User> allUsers, ArrayList<AMedia> allMovies, ArrayList<AMedia> allSeries) {
        this.allMedia = allMedia;
        this.allUsers = allUsers;
        this.allMovies = allMovies;
        this.allSeries = allSeries;
    }

    public void runMainMenu() {

        System.out.println("Welcome " + ProgramControl.currentUser.getUsername() + ", please enter an option below.");
        while (true) {
            System.out.println("1 - Get suggestions.");
            System.out.println("2 - Search.");
            System.out.println("3 - See list of your saved media.");
            System.out.println("4 - See list of media you have already watched.");
            System.out.println("5 - Log out.");
            System.out.println("6 - User options.");
            String userInput = scanner.nextLine();
            switch (userInput) {
                case "1":
                    suggestedMedia();
                    break;
                case "2":
                    searchEngine();
                    break;
                case "3":
                    searchBySavedMedia();
                    break;
                case "4":
                    searchByWatchedMedia();
                    break;
                case "5":
                    logOut();
                    break;
                case "6":
                    userOptions();
                    break;
                default:
                    System.out.println("The option you have chosen does not exist.\n" + "Please try again: ");
            }
        }
    }

    private void suggestedMedia() {
        ArrayList<AMedia> allMoviesAltered = allMovies;
        String chosenCategory = "";
        AMedia suggestion1 = allMoviesAltered.get(rnd.nextInt(0, allMoviesAltered.size()));
        AMedia suggestion2 = allMoviesAltered.get(rnd.nextInt(0, allMoviesAltered.size()));
        AMedia suggestion3 = allMoviesAltered.get(rnd.nextInt(0, allMoviesAltered.size()));
        if (ProgramControl.currentUser.getWatchedMedia().size() > 0) {
            //Finding the last played media from the current user.
            AMedia lastPlayedMedia = ProgramControl.currentUser.getWatchedMedia().get(ProgramControl.currentUser.getWatchedMedia().size() - 1);
            //Finding the category/categories of the last played media and picking a random category between them.
            String lastPlayedCategory = lastPlayedMedia.getCategory();
            String[] lastPlayedCategories = lastPlayedCategory.split(", ");


            chosenCategory = getRandomCategory(lastPlayedCategories);
            //Creating a list of media that has the same category as the randomly chosen categories, and picking a random media from the list. Do this for all 3 randomly chosen categories.
            suggestion1 = getRandomMediaFromCategory(chosenCategory, allMoviesAltered);
            chosenCategory = getRandomCategory(lastPlayedCategories);
            allMoviesAltered.remove(suggestion1);
            suggestion2 = getRandomMediaFromCategory(chosenCategory, allMoviesAltered);
            chosenCategory = getRandomCategory(lastPlayedCategories);
            allMoviesAltered.remove(suggestion2);
            suggestion3 = getRandomMediaFromCategory(chosenCategory, allMoviesAltered);
        }
        if(ProgramControl.currentUser.getAge() < 18){
            chosenCategory = "Family";
            suggestion1 = getRandomMediaFromCategory(chosenCategory, allMoviesAltered);
            suggestion2 = getRandomMediaFromCategory(chosenCategory, allMoviesAltered);
            suggestion3 = getRandomMediaFromCategory(chosenCategory, allMoviesAltered);
        }
        System.out.println("We have found these options you might like: ");
        System.out.println("1 - " + suggestion1.getName() + ".");
        System.out.println("2 - " + suggestion2.getName() + ".");
        System.out.println("3 - " + suggestion3.getName() + ".\n");
        System.out.println("4 - Go back to main menu.");
        String userInput = scanner.nextLine().trim();
        switch (userInput) {
            case "1":
                suggestion1.chooseMedia();
                break;
            case "2":
                suggestion2.chooseMedia();
                break;
            case "3":
                suggestion3.chooseMedia();
                break;
            case "4":
                runMainMenu();
                break;
            default:
                System.out.println("The option you have entered does not exist.\nPlease try again.");
                suggestedMedia();
        }
    }

    private AMedia getRandomMediaFromCategory(String category, ArrayList<AMedia> allMoviesAltered) {
        ArrayList<AMedia> listOfMediaFromCategory = new ArrayList<>();
        for (AMedia m : allMoviesAltered) {
            if (m.getCategory().contains(category)) {
                listOfMediaFromCategory.add(m);
            }
        }
        if (listOfMediaFromCategory.size() > 0) {
            int randomMediaFromList = rnd.nextInt(0, listOfMediaFromCategory.size());
            return listOfMediaFromCategory.get(randomMediaFromList);
        }
        return allMoviesAltered.get(rnd.nextInt(0, allMoviesAltered.size()));
    }

    private String getRandomCategory(String[] lastPlayedCategories) {
        int randomCategory = rnd.nextInt(0, lastPlayedCategories.length);
        return lastPlayedCategories[randomCategory];
    }

    public void logOut() {
        FileHandling.writeToUserFile("Data/UserData.txt", allUsers);
        System.out.println("We look forward to see you again!");
        try {
            sleep(5000);
        } catch (InterruptedException e) {

        }
        for (int i = 0; i < 25; i++) {
            System.out.println("\n");
        }
        ProgramControl pc = new ProgramControl();
        pc.runProgram();
    }


    //*********
//Search Engine Start
//*********
    private void searchEngine() {
        System.out.println("Search options: ");
        System.out.println("1 - by Movie: ");
        System.out.println("2 - by Series: ");
        System.out.println("3 - to return to Main");
        while (true) {
            String userInput = scanner.nextLine();
            if (userInput.equals("1")) {
                searchMovies(allMovies);
            }
            if (userInput.equals("2")) {
                searchSeries(allSeries);
            }
            if (userInput.equals("3")) {
                runMainMenu();
            }
            System.out.println("The option you have chosen does not exist.\n" + "Please try again: ");
        }

    }

    //*********
//Overview over Movie search options
//*********
    private AMedia searchMovies(ArrayList<AMedia> allMovies) {
        if(ProgramControl.currentUser.getAge() < 18){
            ArrayList<AMedia> listOfFamilyMovies = new ArrayList<>();
            for(AMedia m: allMovies){
                if(m.getCategory().contains("Family")){
                    listOfFamilyMovies.add(m);
                }
            }
            allMovies = listOfFamilyMovies;
        }
        System.out.println("Movies: ");
        System.out.println("1 - by Name: ");
        System.out.println("2 - by Category: ");
        System.out.println("3 - by Rating: ");
        System.out.println("4 - by Year of release: ");
        System.out.println("5 - to return to Search: ");
        while (true) {
            String userInput = scanner.nextLine();

            if (userInput.equals("1")) {
                searchByMediaName(allMovies);
            }
            if (userInput.equals("2")) {
                searchByCategory(allMovies, allcategories);
            }
            if (userInput.equals("3")) {
                searchByRating(allMovies);
            }
            if (userInput.equals("4")) {
                searchByYear(allMovies);
            }
            if (userInput.equals("5")) {
                searchEngine();
            }
            System.out.println("The option you have chosen does not exist.\n" + "Please try again: ");
        }
    }

    public void userOptions() {
        System.out.println("You have these options to alter your user settings:");
        System.out.println("1 - Change username.");
        System.out.println("2 - Change password.");
        System.out.println("3 - Delete account.");
        System.out.println("4 - Return to main menu.");
        String userInput = scanner.nextLine();
        switch (userInput) {
            case "1":
                System.out.println("Please enter your new username or press B go back: ");
                String username = scanner.nextLine();
                if (username.equalsIgnoreCase("b")) {
                    userOptions();
                }
                ProgramControl.currentUser.setUsername(username);
                break;
            case "2":
                System.out.println("Please enter your new username or press B go back: ");
                String password = scanner.nextLine();
                if (password.equalsIgnoreCase("b")) {
                    userOptions();
                }
                ProgramControl.currentUser.setPassword(password);
                break;
            case "3":
                System.out.println("Are you sure that you want to delete your account?");
                System.out.println("Press y to delete your account - this can not be undone.");
                System.out.println("Press any other key to go back.");
                String userChoice = scanner.nextLine();
                if (userChoice.equalsIgnoreCase("y")) {
                    System.out.println("Thank you for trying EverythingMedia.");
                    allUsers.remove(ProgramControl.currentUser);
                    logOut();
                } else {
                    userOptions();
                }
                break;
            case "4":
                runMainMenu();
                break;
            default:
                System.out.println("The option you have chosen does not exist. Please try again.");
                userOptions();
        }
    }

    //*********
//Overview over Series search options
//*********
    private AMedia searchSeries(ArrayList<AMedia> allSeries) {
        if(ProgramControl.currentUser.getAge() < 18){
            ArrayList<AMedia> listOfFamilySeries = new ArrayList<>();
            for(AMedia m: allSeries){
                if(m.getCategory().contains("Family")){
                    listOfFamilySeries.add(m);
                }
            }
            allSeries = listOfFamilySeries;
        }
        System.out.println("Series: ");
        System.out.println("1 - by Name: ");
        System.out.println("2 - by Category: ");
        System.out.println("3 - by Rating: ");
        System.out.println("4 - by Year of release: ");
        System.out.println("5 - to return to Search: ");
        while (true) {
            String userInput = scanner.nextLine();
            if (userInput.equals("1")) {
                searchByMediaName(allSeries);
            }
            if (userInput.equals("2")) {
                searchByCategory(allSeries, allcategories);

            }
            if (userInput.equals("3")) {
                searchByRating(allSeries);
            }
            if (userInput.equals("4")) {
                searchByYear(allSeries);
            }

            if (userInput.equals("5")) {
                searchEngine();
            }
            System.out.println("The option you have chosen does not exist.\n" + "Please try again: ");
        }
    }


    //*********
//Search by Name
//*********
    private void searchByMediaName(ArrayList<AMedia> listOfMedia) {

        System.out.println("Please enter a title you wish to see: ");
        String userInput = scanner.nextLine();
        ArrayList<AMedia> mediaByName = new ArrayList<>();
        for (AMedia m : listOfMedia) {
            if (m.getName().toLowerCase().contains(userInput.toLowerCase())) {
                mediaByName.add(m);
            }

        }
        System.out.println("You can choose the following: \n");

        for (int i = 0; i < mediaByName.size(); i++) {
            System.out.println(i + 1 + " - " + mediaByName.get(i).getName());
        }
        String input2 = scanner.nextLine();
        int userInput2 = -1;
        try {
            userInput2 = Integer.parseInt(input2);
            if (userInput2 > mediaByName.size() || userInput2 <= 0) {
                System.out.println("The chosen option doesn't exist, please try again.");
                searchByMediaName(listOfMedia);

            }

        } catch (Exception e) {
            System.out.println("The chosen option doesn't exist, please try again. ");
            searchByMediaName(listOfMedia);
        }
        mediaByName.get(userInput2 - 1).chooseMedia();
    }

    //*********
//Choose a Category to search from
//*********
    private void searchByCategory(ArrayList<AMedia> listOfMedia, ArrayList<String> allCategories) {
        ArrayList<AMedia> listOfMediaAltered = new ArrayList<>();
        ArrayList<String> listOfCategoriesAltered = new ArrayList<>();
        if(ProgramControl.currentUser.getAge() < 18){
            listOfCategoriesAltered.add("Family");
            for(AMedia m: listOfMedia){
                if(m.getCategory().contains("Family")){
                    listOfMediaAltered.add(m);
                    String[] mCategories = m.getCategory().split(", ");
                    for(int i = 0; i < mCategories.length; i++){
                        if(!listOfCategoriesAltered.contains(mCategories[i])){
                            listOfCategoriesAltered.add(mCategories[i]);
                        }
                    }
                }
            }
        }
        if(ProgramControl.currentUser.getAge() >= 18){
            listOfMediaAltered = listOfMedia;
            listOfCategoriesAltered = allCategories;
        }
        System.out.println("Choose your desired Category: ");
        for (int i = 0; i < listOfCategoriesAltered.size(); i++) {
            System.out.println(i + 1 + " - " + listOfCategoriesAltered.get(i));
        }
        String chooseCategory = scanner.nextLine();
        int chosenCategory = -1;
        try {
            chosenCategory = Integer.parseInt(chooseCategory);
            if (chosenCategory != -1) {
                searchByChosenCategory(listOfCategoriesAltered.get(chosenCategory - 1), listOfMediaAltered);
            }
        } catch (Exception e) {
            System.out.println("The option you have chosen is not valid. Please try again.");
            searchByCategory(listOfMedia, allCategories);
        }
    }

    //*********
//Search by ChosenCategory
//*********
    private void searchByChosenCategory(String chosenCategory, ArrayList<AMedia> listOfMedia) {
        ArrayList<AMedia> MediaFromCategory = new ArrayList<>();

        for (AMedia m : listOfMedia) {
            if (m.getCategory().contains(chosenCategory)) {
                MediaFromCategory.add(m);
            }

        }
        System.out.println("Please choose a desired movie from the list");
        for (int i = 0; i < MediaFromCategory.size(); i++) {
            System.out.println(i + 1 + " - " + MediaFromCategory.get(i).getName());
        }
        String selectedInput = scanner.nextLine();
        int userInput = -1;
        try {
            userInput = Integer.parseInt(selectedInput);
            if (userInput > MediaFromCategory.size() || userInput <= 0) {
                System.out.println("The chosen option doesn't exist, please try again.");
                searchByChosenCategory(chosenCategory, listOfMedia);

            }

        } catch (Exception e) {
            System.out.println("The chosen option doesn't exist, please try again. ");
            searchByChosenCategory(chosenCategory, listOfMedia);
        }
        MediaFromCategory.get(userInput - 1).chooseMedia();
    }

    //*********
//Search by Rating
//*********
    private void searchByRating(ArrayList<AMedia> Media) {
        System.out.println("Please enter the minimum rating of movies you wish to see.");
        String userInput = scanner.nextLine();
        double minimumRating = -1.0;
        try {
            if (userInput.length() == 1) {
                userInput += ".0";
            }

            minimumRating = Double.parseDouble(userInput.replace(",", "."));
            if (minimumRating <= 0 || minimumRating > 10) {
                System.out.println("The typed Rating is either too low or too high, please try again: ");
                searchByRating(Media);
            }

        } catch (Exception e) {
            System.out.println("The typed Rating doesn't exist, please try again: ");
            searchByRating(Media);
        }
        ArrayList<AMedia> mediaByRating = new ArrayList<>();
        for (AMedia m : Media) {

            if (m.getRating() >= minimumRating) {
                mediaByRating.add(m);

            }

        }
        System.out.println("Choose what you would like to watch: ");
        for (int i = 0; i < mediaByRating.size(); i++) {
            System.out.println(i + 1 + " - " + mediaByRating.get(i).getName());
        }
        String selectedInput = scanner.nextLine();
        int userInput2 = -1;
        try {
            userInput2 = Integer.parseInt(selectedInput);
            if (userInput2 > mediaByRating.size() || userInput2 <= 0) {
                System.out.println("The chosen option doesn't exist, please try again.");
                searchByRating(Media);

            }

        } catch (Exception e) {
            System.out.println("The chosen option doesn't exist, please try again. ");
            searchByRating(Media);
        }
        mediaByRating.get(userInput2).chooseMedia();

    }

    //*********
//Search by Year of release
//*********
    private void searchByYear(ArrayList<AMedia> Media) {
        System.out.println("Please enter the desired year of release.");
        String userInput = scanner.nextLine();
        int yearOfRelease = -1;
        try {

            yearOfRelease = Integer.parseInt(userInput);
            if (yearOfRelease == -1) {
                System.out.println(userInput + " is not a valid year.");
                searchByYear(Media);
            }

        } catch (Exception e) {
            System.out.println("The typed year doesn't exist, please try again: ");
            searchByYear(Media);
        }
        ArrayList<AMedia> mediaByYearOfRelease = new ArrayList<>();
        for (AMedia y : Media) {

            if (y.getReleaseYear() == yearOfRelease || y.getReleaseYear() == yearOfRelease - 1 || y.getReleaseYear() == yearOfRelease + 1) {
                mediaByYearOfRelease.add(y);

            }

        }
        System.out.println("Choose what you would like to watch: ");
        for (int i = 0; i < mediaByYearOfRelease.size(); i++) {
            System.out.println(i + 1 + " - " + mediaByYearOfRelease.get(i).getName());
        }
        String selectedInput = scanner.nextLine();
        int userInput2 = -1;
        try {
            userInput2 = Integer.parseInt(selectedInput);
            if (userInput2 > mediaByYearOfRelease.size() || userInput2 <= 0) {
                System.out.println("The chosen option doesn't exist, please try again.");
                searchByYear(Media);
            }
        } catch (Exception e) {
            System.out.println("The chosen option doesn't exist, please try again. ");
            searchByYear(Media);
        }
        mediaByYearOfRelease.get(userInput2).chooseMedia();
    }

    private void searchBySavedMedia() {
        ArrayList<AMedia> savedMedia = ProgramControl.currentUser.getSavedMedia();
        if (savedMedia.size() == 0) {
            System.out.println("You don't have anything saved.");
            System.out.println("Returning to main menu.");
            try {
                sleep(2500);
            } catch (Exception e) {

            }
            for (int i = 0; i < 25; i++) {
                System.out.println("\n");
            }
            runMainMenu();
        }
        System.out.println("What would you like to watch from your saved list?");
        int i;
        for (i = 0; i < savedMedia.size(); i++) {
            System.out.println(i + 1 + " - " + savedMedia.get(i).getName());
        }
        System.out.println("\n" +(i+1) + " - Return to main menu.");
        String userInput = scanner.nextLine();
        int chosenMedia = -1;
        try {
            chosenMedia = Integer.parseInt(userInput);
            if(chosenMedia == i+1){
                runMainMenu();
            }
            if (chosenMedia < 0 || chosenMedia > savedMedia.size()) {
                System.out.println("The chosen option doesn't exist, please try again.");
                searchBySavedMedia();
            }
        } catch (Exception e) {
            System.out.println("The chosen option doesn't exist, please try again. ");
            searchBySavedMedia();
        }
        savedMedia.get(chosenMedia - 1).chooseMedia();
    }

    private void searchByWatchedMedia() {
        ArrayList<AMedia> watchedMedia = ProgramControl.currentUser.getWatchedMedia();
        if (watchedMedia.size() == 0) {
            System.out.println("You haven't watched anything yet.");
            System.out.println("Returning to main menu.");
            try {
                sleep(2500);
            } catch (Exception e) {

            }
            for (int i = 0; i < 25; i++) {
                System.out.println("\n");
            }
            runMainMenu();
        }
        System.out.println("What would you like to watch again?");
        int i;
        for (i = 0; i < watchedMedia.size(); i++) {
            System.out.println(i + 1 + " - " + watchedMedia.get(i).getName());
        }
        System.out.println("\n" + (i+1) + " - Return to main menu.");
        String userInput = scanner.nextLine();
        int chosenMedia = -1;
        try {
            chosenMedia = Integer.parseInt(userInput);
            if(chosenMedia == i+1){
                runMainMenu();
            }
            if (chosenMedia < 0 || chosenMedia > watchedMedia.size()) {
                System.out.println("The chosen option doesn't exist, please try again.");
                searchByWatchedMedia();
            }
        } catch (Exception e) {
            System.out.println("The chosen option doesn't exist, please try again. ");
            searchByWatchedMedia();
        }
        watchedMedia.get(chosenMedia - 1).chooseMedia();
    }
}