import pandas as pd
import numpy as np
from matplotlib import pyplot as plt
import seaborn as sns
from .models import Profile, Groups
from .serializers import GroupsSerializer
from sklearn.model_selection import train_test_split
from sklearn.neighbors import KNeighborsClassifier
from sklearn.cluster import KMeans
from sklearn.metrics import confusion_matrix
from joblib import dump, load
from .my_database import DatabaseHelper

class Knn():
    #zamiana wartości atrybutów ze stringów na kolejne inty w celu póżniejszej klasyfikacji 
    def preprocessData(df, list, col):
        df[col] = df[col].apply(lambda x: list.index(df[col][df[col].tolist().index(x)]))

    #metoda zwracająca stringa złożonego z 0 i 1 na podstawie tupli złożonej z 3 intów potrzbnego do 1HotEncodingu
    #w celu identyfikacji przykładowwo trójki (actor1, actor2, actor3)
    def createStr(ind1, ind2, ind3, length):
        str = ""
        for x in range(length):
            if (x == ind1 or x == ind2 or x == ind3):
                str += "1"
            else:
                str += "0"
        return str

    #OneHotEncoding dla trójkowych wartości atrybutów
    def oneHotEncoding(df, base):
        oldCols = ["actor", "country", "director", "elem", "food", "movie", "years"]
        newCols = ["actors", "countries", "directors", "elements", "foods", "movies", "years"]
        newBase = [base[0], base[1], base[2], base[3], base[4], base[6], base[9]]
        newList = []
        newDf = []

        #stworzenie nowych atrybutów (np actors zamiast actor1, actor2, actor3)
        for y in range(len(newCols)): 
            for x in range(len(df[oldCols[y] + "1"])):
                str = Knn.createStr(df[oldCols[y] + "1"][x], df[oldCols[y] + "2"][x], df[oldCols[y] + "3"][x], len(newBase[y]))
                newList.append(str)
            df2 = pd.DataFrame(newList, columns=[newCols[y]])
            newList = []
            newDf.append(df2[newCols[y]])
        
        #dodanie nowych atrybutów do df
        df = df.assign(actors=newDf[0], countries=newDf[1], directors=newDf[2], elements=newDf[3], 
        foods=newDf[4], movies=newDf[5], years=newDf[6])

        #usunięcie starych atrybutów
        for x in oldCols:
            del df[x + "1"], df[x + "2"], df[x + "3"]

        return df

    #uczenie się modelu na podstawie nowych profili
    def learn():
        cols = [field.name for field in Profile._meta.get_fields()]
        cols = cols[2:]
        cols.sort() #atrybuty modelu
        data = DatabaseHelper.getProfiles()[0::2]  #wartości atrybutów
        users = DatabaseHelper.getProfiles()[1::2]

        df = pd.DataFrame(data, columns=cols) #stworzenie Dataframe'u
        pd.set_option('display.max_rows', None, 'display.max_columns', None)
        print(df)

        columns = len(cols)  #ilość kolumn
        sizeDf = df.size     #ilość rzędów

        base = DatabaseHelper.getBase()  #szablon bazy wyborów
        headers = cols
        headers.remove("age")
        headers.remove("oscar")     #do preprocessingu niepotrzbne wartości które juz zakfalifikowane (boolean, int)
        
        #preprocessing danych (zamiana wszystkich stringów na inty)
        y = 0
        x = 0
        while x < len(headers):
            if (headers[x] == "sex" or headers[x] == "group" or headers[x] == "place"):
                Knn.preprocessData(df, base[y], headers[x])
                y += 1
                x += 1
            else:
                for z in range(3):
                    Knn.preprocessData(df, base[y], headers[x])
                    x += 1
                y += 1
        print(df)
        
        #1 hot encoding
        df = Knn.oneHotEncoding(df, base)
        print(df)

        #rzędy do danych treningowych i testowych
        rows = sizeDf/columns
        rowsLim = int(rows/3*2)
        
        #podział na dane treningowe i testowe
        #X_train = df.iloc[:rowsLim, :]
        X_train = df
        #X_test = df.iloc[rowsLim:, :]
        
        #utworzenie modelu algorytmem Kmeans (uczenie nienadzorowane) o określonej liczbie grup 
        #przewidzenie klasyfikacji na podstawie modelu Kmeans
        kmeans = KMeans(n_clusters=4)
        kmeans.fit(X_train)
        y_kmeans = kmeans.predict(X_train)
        #y_kmeans_test = kmeans.predict(X_test)
        print(y_kmeans)
        #print(y_kmeans_test)
    
        #utworzenie modelu algorytmem KNN (uczenie nadzorowane) na podstawie grup utworzonych Kmeansem
        m = KNeighborsClassifier()
        m.fit(X_train, y_kmeans)

        #przewidzenie klasyfikacji na podstawie modelu KNN
        #y_predicted = m.predict(X_test)
        y_predicted = m.predict(X_train)
        print(y_predicted)
        #print(m.score(X_test, y_kmeans_test)) 
        print(m.score(X_train, y_kmeans))

        #zapisanie modeli
        dump(m, 'model_knn.dmp')
        dump(kmeans, 'model_kmeans.dmp')

        #zapisanie przewidzianych grup użytkowników do bazy danych
        i = 0
        for x in users:
            profile = Groups(knn=y_predicted[i], kmeans=y_kmeans[i])
            serializer = GroupsSerializer(profile)
            DatabaseHelper.updateGrups(x, serializer.data)
            i += 1

    def setGroup():
        
        #załadowanie modeli
        knn_model = load('model_knn.dmp')
        kmeans_model = load('model_kmeans.dmp')
        
        cols = [field.name for field in Profile._meta.get_fields()]
        cols = cols[2:]
        cols.sort()  #atrybuty modelu
        userID = DatabaseHelper.uID  #uid klasyfikowanego użytkownika
        if (DatabaseHelper.groupsExist(userID) == False):  #jeżeli jeszcze nie jest sklasyfikowany
            data = DatabaseHelper.getProfileToClassify(userID)  #wartości atrybutów

            df = pd.DataFrame(data, columns=cols)
            pd.set_option('display.max_rows', None, 'display.max_columns', None)

            print(df)

            base = DatabaseHelper.getBase()  #szablon bazy wyborów
            headers = cols
            headers.remove("age")
            headers.remove("oscar")  #do preprocessingu niepotrzbne wartości które juz zakfalifikowane (boolean, int)
            
            #preprocessing danych (zamiana wszystkich stringów na inty)
            y = 0
            x = 0
            while x < len(headers):
                if (headers[x] == "sex" or headers[x] == "group" or headers[x] == "place"):
                    Knn.preprocessData(df, base[y], headers[x])
                    y += 1
                    x += 1
                else:
                    for z in range(3):
                        Knn.preprocessData(df, base[y], headers[x])
                        x += 1
                    y += 1

            print(df)

            #1 hot encoding
            df = Knn.oneHotEncoding(df, base)
            print(df)

            X = df 
            y_kmeans = kmeans_model.predict(X) #przewidzenie grupy użytkownika wg Kmeans'a
            y_knn = knn_model.predict(X)  #przewidzenie grupy użytkownika wg Kmeansa a potem KNN'a
            print(y_kmeans)
            print(y_knn)

            users = DatabaseHelper.getUsersGroups()[1::2] #uid wszystkich użytkowników
            knn_groups = [] #grupy KNN użytkowników
            kmeans_groups = []  #grupy Kmeans użytkowników
            listGroups = DatabaseHelper.getUsersGroups()[0::2]
            for x in listGroups:
                kmeans_groups.append(x[0])
                knn_groups.append(x[1])

            #lista indeksów użytkowników dopasowanych
            matchesKmeans = [ i for i in range(len(kmeans_groups)) if kmeans_groups[i] == y_kmeans[0] ]
            matchesKnn = [ i for i in range(len(knn_groups)) if knn_groups[i] == y_knn[0] ]
            print(matchesKmeans)
            print(matchesKnn)

            #lista uid użytkowników dopasowanych
            matchedUsersKmeans = [ users[i] for i in matchesKmeans ]
            matchedUsersKnn = [ users[i] for i in matchesKnn ]
            print(matchedUsersKmeans)
            print(matchedUsersKnn)

            #zapisanie grupy sklasyfikowanego użytkownika do bazy danych
            groups = Groups(knn=y_knn[0], kmeans=y_kmeans[0])
            serializer = GroupsSerializer(groups)
            DatabaseHelper.updateGrups(userID, serializer.data)

            # update modelu
            
            #zwrócenie uid dopasowanych użytkowników
            #return matchedUsersKmeans
            return matchedUsersKnn

        else: #jeżeli użytkownik jest już sklasyfikowany
            users = DatabaseHelper.getUsersGroups()[1::2]
            ind = users.index(userID) #indeks w users obsługiwanego użytkownika
            del users[ind] #uid wszystkich użytkowników poza obsługiwanym
            knn_groups = []
            kmeans_groups = []
            listGroups = DatabaseHelper.getUsersGroups()[0::2]
            for x in listGroups:
                kmeans_groups.append(x[0])
                knn_groups.append(x[1])
            
            group_kmeans = kmeans_groups[ind] #grupa Kmeans obsługiwanego użytkownika
            group_knn = knn_groups[ind] #grupa KNN obsługiwanego użytkownika
            
            del kmeans_groups[ind] #grupy Kmeans wszystkich użytkowników poza obsługiwanym
            del knn_groups[ind]  #grupy KNN wszystkich użytkowników poza obsługiwanym

            #lista indeksów użytkowników dopasowanych
            matchesKmeans = [ i for i in range(len(kmeans_groups)) if kmeans_groups[i] == group_kmeans ]
            matchesKnn = [ i for i in range(len(knn_groups)) if knn_groups[i] == group_knn ]
            print(matchesKmeans)
            print(matchesKnn)

            #lista uid użytkowników dopasowanych
            matchedUsersKmeans = [ users[i] for i in matchesKmeans ]
            matchedUsersKnn = [ users[i] for i in matchesKnn ]
            print(matchedUsersKmeans)
            print(matchedUsersKnn)

            # update modelu
            
            #zwrócenie uid dopasowanych użytkowników
            #return matchedUsersKmeans
            return matchedUsersKnn
