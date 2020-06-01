
#NOTE: Overwatch League added 6 new teams for the second season. Failures in some teams are due to them not having a past
#(and thus elo) in the league.

import pygal
from selenium import webdriver
import time
import requests


teams = {"San Francisco Shock": 0, "London Spitfire": 0, "Philadelphia Fusion": 0, "Los Angeles Gladiators": 0,
         "Los Angeles Valiant": 0, "Shanghai Dragons": 0, "Dallas Fuel": 0, "Seoul Dynasty": 0, "Houston Outlaws": 0,
         "New York Excelsior": 0, "Boston Uprising": 0, "Florida Mayhem": 0}

#searches all webpages with games (and some without) to find the winners of each OWL match.
def getAllGames():
    import csv
    result_array = []

    #This has to be the location of chrome driver on the computer
    driver = webdriver.Chrome('/Users/samrosenberg/Desktop/ATCS/masgrebnesor_S1_ATCSy3/chromedriver1')  # Optional argument, if not specified will search path.
    with open('ow.csv', mode='w') as csv_file:
        #each game has a winner, loser, id and week. These are saved to ow.csv
        fieldnames = ['win', 'loss', 'game', "week"]
        writer = csv.DictWriter(csv_file, fieldnames=fieldnames)
        writer.writeheader()

        # go to the first game page
        #this uses Selenium to properly search the Javascript.
        id = 10223
        for id in range(10223, 10632):
            driver.get("https://overwatchleague.com/en-us/match/" + str(id))
            #without a wait, the code will look for an element that hasn't been loaded.
            time.sleep(5)
            try:
                #winning team div
                win_team = driver.find_elements_by_class_name("TeamScore--winner")
                for each in win_team:
                    if len(each.text) > 1:
                        winner = each
                    else:
                        winner = None

                winner = winner.text.splitlines()[0]
                teams[winner] += 1

                #loss (other
                loss_team = driver.find_elements_by_class_name("TeamScore-name")
                for each in loss_team:
                    if not each.text == winner:
                        loser = each.text
                teams[loser] += 1

                result_array.append((winner, loser))

                import csv
                #teams play 2 games a week, so the week a team is currently playing in is rounded up the number they have played rounded up.
                writer.writerow({'win': winner, 'loss': loser, 'game': id, 'week': round(teams[winner]/2)})

            except:
                continue



        driver.quit()

        csv_file.close()

#getAllGames()

#counts game number for each team
game_num = {"San Francisco Shock": 0, "London Spitfire": 0, "Philadelphia Fusion": 0, "Los Angeles Gladiators": 0,
         "Los Angeles Valiant": 0, "Shanghai Dragons": 0, "Dallas Fuel": 0, "Seoul Dynasty": 0, "Houston Outlaws": 0,
         "New York Excelsior": 0, "Boston Uprising": 0, "Florida Mayhem": 0}

#expected score of a given person
def calcES(teamArating, teamBrating):
    return 1 / (1 + 10 ** ((teamArating - teamBrating) / 400))

#from another code written prior and modified to take CSV inputs
#NOTE: modification is part of my code. THE ELO SYSTEM IS NOT MY OWN, AND THE CODE WAS WRITEN PRIOR. They system is
#commonly agreed upon to be the idea 1v1 ranking system, and the mechanics of doing so remain the same almost so it was difficult
#to write in a "different" way.
def elo():
    import csv

    #This is the CSV mod. It writes the team, elo, and game number into a file.
    with open('owelo.csv', mode='w') as csv_file:
        fieldnames = ['team', 'elo', 'game']
        writer = csv.DictWriter(csv_file, fieldnames=fieldnames)
        writer.writeheader()
        games = []
        import csv

        f = open('ow.csv')
        csv_f = csv.reader(f)

        count = 0
        for row in csv_f:
            if not count == 0:
                games.append((row[0], row[1]))

            count += 1

        teams = {}
        #this is the non-original, basic elo calculator
        for game in games:
            for team in game:
                if team not in teams:
                    teams[team] = 1600
            teamA = game[0]
            teamB = game[1]
            ratingA = teams[teamA]
            ratingB = teams[teamB]
            AES = calcES(ratingA, ratingB)
            teams[teamA] = ratingA + 32 * (AES)
            teams[teamB] = ratingB - 32 * (AES)

            game_num[teamA] += 1
            game_num[teamB] += 1

            #more csv stuff
            writer.writerow({'team': teamA, 'elo': teams[teamA], 'game' : game_num[teamA]})
            writer.writerow({'team': teamB, 'elo': teams[teamB], 'game': game_num[teamB]})


#elos of teams in an array
elos = {"San Francisco Shock": [1600], "London Spitfire": [1600], "Philadelphia Fusion": [1600], "Los Angeles Gladiators": [1600],
     "Los Angeles Valiant": [1600], "Shanghai Dragons": [1600], "Dallas Fuel": [1600], "Seoul Dynasty": [1600], "Houston Outlaws": [1600],
     "New York Excelsior": [1600], "Boston Uprising": [1600], "Florida Mayhem": [1600]}

#draws a draph of elo vs games, by pulling data from the csv created above.
def drawGraph():
    import csv

    # gets data and formats to fit a graph.
    f = open('owelo.csv')
    csv_f = csv.reader(f)
    count = 0
    for row in csv_f:
        if not count == 0:
            elos[row[0]].append(float(row[1]))
        count += 1

    line_chart = pygal.Line()
    line_chart.title = 'Elo Rating for teams in Overwatch League by Game'
    line_chart.x_labels = map(str, range(1, 47))
    for key, value in elos.items():
        line_chart.add(key, value)

    #check directory, open file below with chrome (or whatever browser you use) to see intractable graph.
    line_chart.render_to_file('team_based_elo.svg')


nationality_count = {"San Francisco Shock": {}, "London Spitfire": {}, "Philadelphia Fusion": {}, "Los Angeles Gladiators": {},
         "Los Angeles Valiant": {}, "Shanghai Dragons": {}, "Dallas Fuel": {}, "Seoul Dynasty": {}, "Houston Outlaws": {},
         "New York Excelsior": {}, "Boston Uprising": {}, "Florida Mayhem": {}}

import json
#This function gets the nationality of each person on any given team: this creates a list per team.
def getNationalityTeams():
    urlGet = "https://api.overwatchleague.com/teams"
    response = requests.get(urlGet)
    data = json.loads(response.text)
    for each in data["competitors"]:
        for player in each["competitor"]["players"]:
            try:
                if not player["player"]['nationality'] in nationality_count[each['competitor']['name']]:
                    nationality_count[each['competitor']['name']][player["player"]['nationality']] = 1
                else:
                    nationality_count[each['competitor']['name']][player["player"]['nationality']] += 1

            except:
                continue



nationality_counts = {"San Francisco Shock": {}, "London Spitfire": {}, "Philadelphia Fusion": {}, "Los Angeles Gladiators": {},
         "Los Angeles Valiant": {}, "Shanghai Dragons": {}, "Dallas Fuel": {}, "Seoul Dynasty": {}, "Houston Outlaws": {},
         "New York Excelsior": {}, "Boston Uprising": {}, "Florida Mayhem": {}}

nationality_totals = {}

#this function grabs the number of players with each nationality of any given team.
def getNationalityCount():
    for team, countries in nationality_count.items():
        for country, count in countries.items():
            if country not in nationality_totals:
                nationality_totals[country] = count
            else:
                nationality_totals[country] += int(count)
            if country not in nationality_counts[team]:
                nationality_counts[team][country] = count
            else:
                nationality_counts[team][country] = nationality_counts[team][country] + int(count)


#This gets the elo of each nationality for each team and places it in a dictionary
nationality_elo = {}
def nationalityElo():
    #NYXL played the most games
    for game in range(1,len(elos["New York Excelsior"])):
        for team, countries in nationality_counts.items():
                for country, count in countries.items():
                    try:
                        if country not in nationality_elo:
                            nationality_elo[country] = [(elos[team][game], count, game)]
                        else:
                            nationality_elo[country].append((elos[team][game], count, game))
                    #team didn't play as many games as another
                    except IndexError:
                        continue


#average elo per game per country.
avg_elo = {}
def avgElo():
    for country in nationality_elo:
        internal = {}
        #42 is minimum games played
        for game in range(1, 42):
            for team_game in nationality_elo[country]:
                try:
                    if team_game[2] == game:
                        if not game in internal:
                            internal[game] = team_game[0]*team_game[1]
                        else:
                            internal[game] += team_game[0]*team_game[1]

                except IndexError:
                    continue

            internal[game] = internal[game]/nationality_totals[country]

        avg_elo[country] = internal



#formats and graphs country
to_graph = {}
def drawCountryGraph():
    for country in avg_elo:
        for game, elo in avg_elo[country].items():
            if not country in to_graph:
                to_graph[country] = [elo]
            else:
                to_graph[country].append(elo)

    line_chart = pygal.Line()
    line_chart.title = 'Elo Rating for all nationalities in Overwatch League by Game'
    line_chart.x_labels = map(str, range(1, 42))
    for key, value in to_graph.items():
        line_chart.add(key, value)

    #check directory, open file below with chrome (or whatever browser you use) to see intractable graph.
    line_chart.render_to_file('country_based_elo.svg')


#limits the graph to only teams with 3 or more players.
def above3Players():
    for country in avg_elo:
        if nationality_totals[country] >= 3:
            for game, elo in avg_elo[country].items():
                if not country in to_graph:
                    to_graph[country] = [elo]
                else:
                    to_graph[country].append(elo)

    line_chart = pygal.Line()
    line_chart.title = 'Elo Rating for countries with 3 players in Overwatch League by Game'
    line_chart.x_labels = map(str, range(1, 42))
    for key, value in to_graph.items():
        line_chart.add(key, value)

    #check directory, open file below with chrome (or whatever browser you use) to see intractable graph.
    line_chart.render_to_file('3+_elo.svg')




if __name__ == '__main__':
    # put your test code here
    elo()
    drawGraph()
    getNationalityTeams()
    getNationalityCount()
    nationalityElo()
    avgElo()
    drawCountryGraph()
    above3Players()

