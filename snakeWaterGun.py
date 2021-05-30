from random import *
dict1 = {"s": "Snake", "w": "Water", "g": "Gun"}
list1 = ["Snake", "Water", "Gun"]
while(True):
    score_of_player = 0
    score_of_computer = 0
    draw = 0
    print(" SNAKE WATER GUN GAME\n")
    print(" Press s for Snake\n", "Press w for Water\n", "Press g for Gun")
    print("\n")
    i = 0
    while(i < 10):
        a = choice(list1)

        b = input("Enter your choice: ")
        if b == "s":
            print("Your choice is Snake")
        elif b == "w":
            print("Your choice is Water")
        elif b == "g":
            print("Your choice is Gun")
        else:
            print("Invalid input!!")
        try:
            if dict1[b] == a:
                print("Computer choice is ", a)
                print("Draw")
                print("\n")
                draw += 1
                i += 1
            else:
                if dict1[b] == "Snake" and a == "Water" or dict1[b] == "Water" and a == "Gun" or dict1[b] == "Gun" and a == "Snake":
                    print("Computer choice is ", a)
                    print("\n")
                    score_of_player += 1
                    i += 1
                else:
                    print("Computer choice is ", a)
                    print("\n")
                    score_of_computer += 1
                    i += 1
        except Exception as e:
            print("Enter choice from s/w/g")
    if score_of_player > score_of_computer:
        print("Yours Score: ", score_of_player, "\nComputer's score: ",
              score_of_computer, "\nNumber of draws in the match: ", draw)
        print("You won!!!")
    elif score_of_player == score_of_computer:
        print("Yours Score: ", score_of_player, "\nComputer's score: ",
              score_of_computer, "\nNumber of draws in the match: ", draw)
        print("Match Drawn!!!")
    else:
        print("Yours Score: ", score_of_player, "\nComputer's score: ",
              score_of_computer, "\nNumber of draws in the match: ", draw)
        print("Better luck next time!!!")
    s = input("Do you want to play again(y/n): ")
    print("\n")
    if s == "y":
        continue
    else:
        break
