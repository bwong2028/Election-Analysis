package election;

/* 
 * Election Analysis class which parses past election data for the house/senate
 * in csv format, and implements methods which can return information about candidates
 * and nationwide election results. 
 * 
 * It stores the election data by year, state, then election using nested linked structures.
 * 
 * The years field is a Singly linked list of YearNodes.
 * 
 * Each YearNode has a states Circularly linked list of StateNodes
 * 
 * Each StateNode has its own singly linked list of ElectionNodes, which are elections
 * that occured in that state, in that year.
 * 
 * This structure allows information about elections to be stored, by year and state.
 * 
 * @author Colin Sullivan
 */
public class ElectionAnalysis 
{

    // Reference to the front of the Years SLL
    private YearNode years;

    public YearNode years() 
    {
        return years;
    }

    /*
     * Read through the lines in the given elections CSV file
     * 
     * Loop Though lines with StdIn.hasNextLine()
     * 
     * Split each line with:
     * String[] split = StdIn.readLine().split(",");
     * Then access the Year Name with split[4]
     * 
     * For each year you read, search the years Linked List
     * -If it is null, insert a new YearNode with the read year
     * -If you find the target year, skip (since it's already inserted)
     * 
     * If you don't find the read year:
     * -Insert a new YearNode at the end of the years list with the corresponding year.
     * 
     * @param file String filename to parse, in csv format.
     */
    public void readYears(String file) 
    {
		// WRITE YOUR CODE HERE
      StdIn.setFile(file);
      while (StdIn.hasNextLine())
      {
        String[] split = StdIn.readLine().split(",");
        int year = Integer.parseInt(split[4]);
        if (years == null)
        {
          years = new YearNode(year);
        }
        else
        {
          YearNode pointer = years;
          while (pointer.getYear() != year)
          {
            if (pointer.getNext() == null)
            {
              pointer.setNext(new YearNode(year));
              break;
            }
            pointer = pointer.getNext();
          }

        }

      }

        
    }

    /*
     * Read through the lines in the given elections CSV file
     * 
     * Loop Though lines with StdIn.hasNextLine()
     * 
     * Split each line with:
     * String[] split = StdIn.readLine().split(",");
     * Then access the State Name with split[1] and the year with split[4]
     * 
     * For each line you read, search the years Linked List for the given year.
     * 
     * In that year, search the states list. If the target state exists, continue
     * onto the next csv line. Else, insert a new state node at the END of that year's
     * states list (aka that years "states" reference will now point to that new node).
     * Remember the states list is circularly linked.
     * 
     * @param file String filename to parse, in csv format.
     */
    public void readStates(String file) 
    {
		// WRITE YOUR CODE HERE
      StdIn.setFile(file);
      while (StdIn.hasNextLine())
      {
        String[] split = StdIn.readLine().split(",");
        int year = Integer.parseInt(split[4]);
        String state = split[1];

        YearNode ptr = years;

        while (ptr != null)
        {
          if (ptr.getYear() == year)
          {
            StateNode ptr2 = ptr.getStates();
            if (ptr2 == null)
            {
              StateNode n = new StateNode(state, null);
              ptr2 = n;
              n.setNext(n);
              ptr.setStates(ptr2);
              break;
            }
            else
            {
              do {
                ptr2 = ptr2.getNext();
              } while (ptr2 != ptr.getStates() && !ptr2.getStateName().equals(state));

              if (!ptr2.getStateName().equals(state))
              {
                StateNode n = new StateNode(state, ptr.getStates().getNext());
                ptr2.setNext(n);
                ptr.setStates(n);
              }

            }

          }
          ptr = ptr.getNext();
        }
      }
    }


    /*
     * Read in Elections from a given CSV file, and insert them in the
     * correct states list, inside the correct year node.
     * 
     * Each election has a unique ID, so multiple people (lines) can be inserted
     * into the same ElectionNode in a single year & state.
     * 
     * Before we insert the candidate, we should check that they dont exist already.
     * If they do exist, instead modify their information new data.
     * 
     * The ElectionNode class contains addCandidate() and modifyCandidate() methods for you to use.
     * 
     * @param file String filename of CSV to read from
     */
    public void readElections(String file) 
    {
		  // WRITE YOUR CODE HERE
        StdIn.setFile(file);
        while (StdIn.hasNextLine()) 
        {
          String line = StdIn.readLine();
          String[] split = line.split(",");
          int raceID = Integer.parseInt(split[0]);
          String stateName = split[1];
          int officeID = Integer.parseInt(split[2]);
          boolean senate = split[3].equals("U.S. Senate");
          int year = Integer.parseInt(split[4]);
          String canName = split[5];
          String party = split[6];
          int votes = Integer.parseInt(split[7]);
          boolean winner = split[8].toLowerCase().equals("true");
          
          ElectionNode n = new ElectionNode();
          
          n.addCandidate(canName, votes, party, winner);

          n.setSenate(senate);

          n.setRaceID(raceID);
          
          n.setoOfficeID(officeID);
          


          YearNode ptr = years;

          while (ptr != null && ptr.getYear() != year)
          { 
            ptr = ptr.getNext();
          }

          StateNode ptr2 = ptr.getStates().getNext();

          while (!ptr2.getStateName().equals(stateName))
          {
            ptr2 = ptr2.getNext();
            if (ptr2 == ptr.getStates().getNext())
              break;
          }

          ElectionNode ptr3 = ptr2.getElections();

          if (ptr3 == null)
          {
            ptr3 = n;
            ptr2.setElections(ptr3);
            continue;
          }

          while (ptr3 != null)
          {
            if (ptr3.getRaceID() == raceID)
            {
              if (ptr3.isCandidate(canName))
              {
                ptr3.modifyCandidate(canName, votes, party);
                break;
              }
              else
              {
                ptr3.addCandidate(canName, votes, party, winner);
                break;
              }
            }

            if (ptr3.getNext() == null)
            {
              ptr3.setNext(n);
              break;
            }

            ptr3 = ptr3.getNext();
          }
         
        }
    }

    /*
     * DO NOT EDIT
     * 
     * Calls the next method to get the difference in voter turnout between two
     * years
     * 
     * @param int firstYear First year to track
     * 
     * @param int secondYear Second year to track
     * 
     * @param String state State name to track elections in
     * 
     * @return int Change in voter turnout between two years in that state
     */
    public int changeInTurnout(int firstYear, int secondYear, String state) 
    {
        // DO NOT EDIT
        int last = totalVotes(firstYear, state);
        int first = totalVotes(secondYear, state);
        return last - first;
    }

    /*
     * Given a state name, find the total number of votes cast
     * in all elections in that state in the given year and return that number
     * 
     * If no elections occured in that state in that year, return 0
     * 
     * Use the ElectionNode method getVotes() to get the total votes for any single
     * election
     * 
     * @param year The year to track votes in
     * 
     * @param stateName The state to track votes for
     * 
     * @return avg number of votes this state in this year
     */
    public int totalVotes(int year, String stateName) 
    {
      	// WRITE YOUR CODE HERE
          
        int count = 0;
        YearNode ptr = years;

        
        
        while (ptr != null && ptr.getYear() != year)
        { 
          ptr = ptr.getNext();
        }

        if (ptr == null)
          return count;
        

        StateNode ptr2 = ptr.getStates().getNext();

        while (!ptr2.getStateName().equals(stateName))
        {
          ptr2 = ptr2.getNext();
          if (ptr2 == ptr.getStates().getNext())
            break;

        }
        if (!ptr2.getStateName().equals(stateName))
          return count;

        ElectionNode ptr3 = ptr2.getElections();

        while (ptr3 != null)
        {
          count = count + ptr3.getVotes();
          ptr3 = ptr3.getNext();
        }

      	return count;
    }

    /*
     * Given a state name and a year, find the average number of votes in that
     * state's elections in the given year
     * 
     * @param year The year to track votes in
     * 
     * @param stateName The state to track votes for
     * 
     * @return avg number of votes this state in this year
     */
    public int averageVotes(int year, String stateName) 
    {
      	// WRITE YOUR CODE HERE
        int average = 0;
        int countElections = 0;


        YearNode ptr = years;

        if (ptr == null)
          return average;
        
        while (ptr != null && ptr.getYear() != year)
        { 
          ptr = ptr.getNext();
        }
        

        StateNode ptr2 = ptr.getStates().getNext();

        while (!ptr2.getStateName().equals(stateName))
        {
          ptr2 = ptr2.getNext();
          if (ptr2 == ptr.getStates().getNext())
            break;

        }
        if (!ptr2.getStateName().equals(stateName))
          return average;

        ElectionNode ptr3 = ptr2.getElections();

        int total = 0;

        while (ptr3 != null)
        {
          total = total + ptr3.getVotes();
          ptr3 = ptr3.getNext();
          countElections++;
        }

        average = total/countElections;

      	return average;

    }

    /*
     * Given a candidate name, return the party they most recently ran with
     * 
     * Search each year node for elections with the given candidate
     * name. Update that party each time you see the candidates name and
     * return the party they most recently ran with
     * 
     * @param candidateName name to find
     * 
     * @return String party abbreviation
     */
    public String candidatesParty(String candidateName) 
    {
		  // WRITE YOUR CODE HERE
      String party = "";


      for (YearNode ptr = years; ptr != null; ptr = ptr.getNext())
      {
        StateNode ptr2 = ptr.getStates();
        
        int countLoops = 0;

        while (true)
        {
          if (ptr2 == ptr.getStates())
            countLoops = countLoops + 1;
          
          if (countLoops == 2)
            break;
          
          ElectionNode ptr3 = ptr2.getElections();

          while (ptr3 != null)
          {
            if (ptr3.isCandidate(candidateName))
              party = ptr3.getParty(candidateName);
      
            ptr3 = ptr3.getNext();
          } 
          ptr2 = ptr2.getNext();
        }
         
      }
 
      return party;
    }
}