import pyttsx3
import datetime
import speech_recognition as sr
import wikipedia
import webbrowser
import os
import smtplib
# sapi5 is an api from windows for using speech function
engine = pyttsx3.init('sapi5')
voices = engine.getProperty('voices')
# print(voices[1].id)
engine.setProperty('voice', voices[1].id)


def speak(audio):
    engine.say(audio)
    engine.runAndWait()


def wishMe():
    # this will give hours from 0 to 24
    hour = int(datetime.datetime.now().hour)
    if(hour >= 0 and hour < 12):
        speak("Good Morning Rajdeep")
    elif(hour >= 12 and hour < 18):
        speak('Good afternoon Rajdeep')
    else:
        speak("Good Evening Rajdeep")
    speak("I am Python assistant sir, please tell me what can i do for you!!")


def takeCommand():
    '''It takes microphone input from the user and return string output '''
    r = sr.Recognizer()
    #r.energy_threshold = 300
    with sr.Microphone() as source:
        print("Listening....")
        r.adjust_for_ambient_noise(source, duration=5)
        r.pause_threshold = 1  # with the help of threshold we can make our assistant to wait for a second before it consider our phrase completes
        audio = r.listen(source)
    try:
        print("Recognizing....")
        query = r.recognize_google(audio, language='en-in')
        print(f"User said: {query}\n")
    except Exception as e:
        # print(e) #dont show this error in the console
        speak("Say that again please....")
        print("Say that again please....")
        return 'None'
    return query


def sendEmail(to, content):
    server = smtplib.SMTP('smtp.gmail.com', 587)
    server.ehlo()
    server.starttls()
    # you have to write your own id and password
    server.login('youremail@gmail.com', 'your-password')
    server.sendmail('youremail@gmail.com', to, content)
    server.close()


if __name__ == "__main__":
    wishMe()
    while True:

        query = takeCommand().lower()
        # logic for exeuting tasks based on query
        if('wikipedia' in query):
            speak("Searching wikipedia....")
            query = query.replace("wikipedia", "")
            results = wikipedia.summary(query, sentences=2)
            speak("According to wikipedia")
            print(results)
            speak(results)
        elif("open youtube" in query):
            webbrowser.open("youtube.com")
        elif("open google" in query):
            webbrowser.open("google.com")
        elif("open stackoverflow" in query):
            webbrowser.open("stackoverflow.com")
        elif("play music" in query):
            speak("Of which artist...")
            query1 = takeCommand().lower()
            query1 = query1.replace(' ', '-')
            webbrowser.open(f"https://gaana.com/artist/{query1}")
        elif("the time" in query):
            strtime = datetime.datetime.now().strftime("%H:%M:%S")
            speak(f"The time is {strtime}")
        elif("open vs code" in query):
            path = "C:\\Users\\Rajdeep\\AppData\\Local\\Programs\\Microsoft VS Code\\Code.exe"
            os.startfile(path)
        elif("send email" in query):
            try:
                speak("What should i say?")
                # this is for single person only if you want to send email to other person
                to = "srivastavanayandeep.015@gmail.com"
                # then you can create a dictinory with name as keys and id as values.
                sendEmail(to, content)
                speak("Email has been sent!")
            except Exception as e:
                speak("Sorry, your email can't be send, an error occured!!")
        elif('stop' in query):
            exit()
