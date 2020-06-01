""" Project Primus!
    Implement a bot that will intelligently find a Wikipedia link ladder
    between two given pages. You will be graded on both finding the shortest
    path (or one of them, if there are several of equal length), and how fast
    your code runs.

    Known errors: any special chars, like è, as links have a mess of letters
    that can't be checked against the final page. I attempted to solve
    the problem with escape, however it doesn't do as much as would be
    necessary. For now, the html string for the given char would be needed
    in the initial endPage.

    Redirects are also handled poorly, however the code WILL find the endpage link exactly as written in the
    call to wikiladder.

"""

import time
__author__ = "Sam Rosenberg"
import requests
from bs4 import BeautifulSoup
import re

#Page class holds information about what link is stored, and allows
#the ladder to be completed by checking
class Page:
    def __init__(self, link, parent):
        self.parent = parent
        self.link = link
        self.length = len(link)

    def display(self):
        return ("Link: " + self.link + " Parent: " + self.parent.link)


def wikiladder(startPage, endPage):
    """ This function returns a list of the wikipedia links (as page titles)
        that one could follow to get from the startPage to the endPage.
        The returned list includes both the start and end pages.
    """
    return breadthFirstMain(startPage, endPage)

""" An attempt at no duplication of returning to any pages. It made the code slower. 
This function replaces not only the breadth first, but also the formatting of return


def noDuplicates(startPage, endPage):
    This function handles the entirety of a breadth first search, calling oter functions to check every page
    # links in the queue
    checked = []
    links_to_check = []
    # sets the current page to check with the Start Page given by the program
    currentPage = Page(startPage, None)
    # main of program: loops until links_to_check
    done = False
    while not done:
        print("CURRENT PAGE: " + currentPage.link)
        done, new_links = getLinks(currentPage, endPage)
        linksToAdd = objectifyLinks(new_links, currentPage)
        links_to_check = links_to_check + linksToAdd

        if not done:

            nextLink = links_to_check.pop(0)
            if nextLink not in checked:
                currentPage = nextLink
            checked.append(currentPage)

    for each in set(links_to_check):
        print("Checked: " + each.link)
        if endPage.replace("_", " ") == each.link:
            print("yay!")
            toReturn = []
            item = currentPage
            while not item.link == startPage:
                toReturn.append(item.link)
                item = item.parent
            toReturn.append(endPage)
            return [startPage] + toReturn

"""

def breadthFirstMain(startPage, endPage):
    """This function handles the entirety of a breadth first search, calling oter functions to check every page"""
    #links in the queue
    links_to_check = []
    #sets the current page to check with the Start Page given by the program
    currentPage = Page(startPage, None)
    #main of program: loops until links_to_check
    done = False
    #Here is the loop for which the code will run until the final page is found.
    while not done:
        done, new_links = getLinks(currentPage, endPage)
        linksToAdd = objectifyLinks(new_links, currentPage)
        links_to_check = links_to_check + linksToAdd
        if not done:
            currentPage = links_to_check.pop(0)

    return formatReturn(startPage, endPage, links_to_check, currentPage)

#this function returns the chain of links taken to find the shortest
#  distance.
def formatReturn(startPage, endPage, links_to_check, currentPage):
    #appended in reverse order
    toReturnReverse = []
    toReturn = []
    item = currentPage
    while not item.link == startPage:
        toReturnReverse.append(item.link)
        item = item.parent
    for each in toReturnReverse:
        toReturn = [each] + toReturn
    if not currentPage == endPage:
        toReturn.append(endPage)
    return [startPage] + toReturn

#This takes the links and returns them as a "Page" object.
def objectifyLinks(links, parentPage):
    toReturn = []
    for each in links:
        toAdd = Page(each, parentPage)
        #this can allow you to see links and their parent for bug testing
        #print(toAdd.display())
        toReturn.append(toAdd)
    return toReturn

#grabs links off a page, and checks to see if the links are
# the target page.
import html
def getLinks(pageToGet, endPage):
    links = getGoodLinks(pageToGet)
    #without this sorted, each run of the code will change in path
    # and speed. This doesn't make it faster, only more consistent.
    return endPage in links, sorted(links)

"""This getGoodLinks replaces the old one, which used beautiful soup. 
That code should be seen here:
def getGoodLinks(wikiPage):
    try:
        wikiURL = "https://en.wikipedia.org/wiki/" + wikiPage.link
        ret = requests.get(wikiURL)
        html = ret.text
        bsObj = BeautifulSoup(html, "html.parser")
        linkList = bsObj.find("div", {"id": "bodyContent"}).findAll("a",
                                                                    {"href": re.compile("^\/wiki\/[^:]*$")})
        retList = []
        for link in linkList:
            retList.append(link.get("title"))
        return retList
    except requests.exceptions.ConnectionError:
        print("Oops")
"""

#grabs all links using regex
#IMPORTANT: regex fails when html link has special symbols.

import html as potato
def getGoodLinks(wikiPage):
    try:
        getLinksRegex = re.compile('"\/wiki\/([^:]+?)"')
        wikiURL = "https://en.wikipedia.org/wiki/" + wikiPage.link
        ret = requests.get(wikiURL)
        html = ret.text
        #this only grabs 1 of each page if linked more than once on
        # a given page. It can grab mainpage, so it is then removed.
        links = []
        links = set(getLinksRegex.findall(html))
        links.remove("Main_Page")
        return links
    #dropped internet catch
    except requests.exceptions.ConnectionError:
        return []


if __name__ == '__main__':
    # put your test code here
    start_time = time.time()
    print(wikiladder("Iraq_War", "Aristotle"))
    print("--- %s seconds ---" % (time.time() - start_time))

