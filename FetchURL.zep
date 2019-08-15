prog FetchURL{
begin
    StringBuilder content = new StringBuilder();
    
    URL url = new URL("https://wgilreath.github.io/WillHome.html");
    URLConnection urlConnection = url.openConnection();

    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
    String line = EMPTY_STRING;

    while ((line = bufferedReader.readLine()) != null){
        content.append(line + "\n");
    }//end while
    bufferedReader.close();
    
    println(content.toString());
    exit(0);

}//end prog FetchURL