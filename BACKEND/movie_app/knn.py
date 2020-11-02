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
    def preprocessData(df, list, col):
        df[col] = df[col].apply(lambda x: list.index(df[col][df[col].tolist().index(x)]))

    def createStr(ind1, ind2, ind3, length):
        str = ""
        for x in range(length):
            if (x == ind1 or x == ind2 or x == ind3):
                str += "1"
            else:
                str += "0"
        return str

    def learn():
        cols = [field.name for field in Profile._meta.get_fields()]
        cols = cols[2:]
        cols.sort()
        data = DatabaseHelper.getProfiles()[0::2]
        users = DatabaseHelper.getProfiles()[1::2]

        df = pd.DataFrame(data, columns=cols)
        pd.set_option('display.max_rows', None, 'display.max_columns', None)
        
        print(df)
        columns = len(cols)
        sizeDf = df.size

        base = DatabaseHelper.getBase()
        headers = cols
        headers.remove("age")
        headers.remove("oscar")
        
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
        
        oldCols = ["actor", "country", "director", "elem", "food", "movie", "years"]
        newCols = ["actors", "countries", "directors", "elements", "foods", "movies", "years"]
        newBase = [base[0], base[1], base[2], base[3], base[4], base[6], base[9]]
        newList = []
        newDf = []
        for y in range(len(newCols)): 
            for x in range(len(df[oldCols[y] + "1"])):
                str = Knn.createStr(df[oldCols[y] + "1"][x], df[oldCols[y] + "2"][x], df[oldCols[y] + "3"][x], len(newBase[y]))
                newList.append(str)
            df2 = pd.DataFrame(newList, columns=[newCols[y]])
            newList = []
            newDf.append(df2[newCols[y]])
        
        df = df.assign(actors=newDf[0], countries=newDf[1], directors=newDf[2], elements=newDf[3], 
        foods=newDf[4], movies=newDf[5], years=newDf[6])

        for x in oldCols:
            del df[x + "1"], df[x + "2"], df[x + "3"]

        print(df)

        rows = sizeDf/columns
        rowsLim = int(rows/3*2)
        
        #X_train = df.iloc[:rowsLim, :]
        X_train = df
        #X_test = df.iloc[rowsLim:, :]
        
        kmeans = KMeans(n_clusters=4)
        kmeans.fit(X_train)
        y_kmeans = kmeans.predict(X_train)
        #y_kmeans_test = kmeans.predict(X_test)
        print(y_kmeans)
        #print(y_kmeans_test)
    
        m = KNeighborsClassifier()
        m.fit(X_train, y_kmeans)

        #y_predicted = m.predict(X_test)
        y_predicted = m.predict(X_train)
        print(y_predicted)
        #print(m.score(X_test, y_kmeans_test)) 
        print(m.score(X_train, y_kmeans))

        dump(m, 'model_knn.dmp')
        dump(kmeans, 'model_kmeans.dmp')

        i = 0
        for x in users:
            profile = Groups(knn=y_predicted[i], kmeans=y_kmeans[i])
            serializer = GroupsSerializer(profile)
            DatabaseHelper.updateGrups(x, serializer.data)
            i += 1

    def setGroup():
        
        knn_model = load('model_knn.dmp')
        kmeans_model = load('model_kmeans.dmp')
        
        cols = [field.name for field in Profile._meta.get_fields()]
        cols = cols[2:]
        cols.sort()
        userID = DatabaseHelper.uID
        if (DatabaseHelper.groupsExist(userID) == False):
            data = DatabaseHelper.getProfileToClassify(userID)

            df = pd.DataFrame(data, columns=cols)
            pd.set_option('display.max_rows', None, 'display.max_columns', None)

            print(df)

            base = DatabaseHelper.getBase()
            headers = cols
            headers.remove("age")
            headers.remove("oscar")
            
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
        
            oldCols = ["actor", "country", "director", "elem", "food", "movie", "years"]
            newCols = ["actors", "countries", "directors", "elements", "foods", "movies", "years"]
            newBase = [base[0], base[1], base[2], base[3], base[4], base[6], base[9]]
            newList = []
            newDf = []
            for y in range(len(newCols)): 
                for x in range(len(df[oldCols[y] + "1"])):
                    str = Knn.createStr(df[oldCols[y] + "1"][x], df[oldCols[y] + "2"][x], df[oldCols[y] + "3"][x], len(newBase[y]))
                    newList.append(str)
                df2 = pd.DataFrame(newList, columns=[newCols[y]])
                newList = []
                newDf.append(df2[newCols[y]])
            
            df = df.assign(actors=newDf[0], countries=newDf[1], directors=newDf[2], elements=newDf[3], 
            foods=newDf[4], movies=newDf[5], years=newDf[6])

            for x in oldCols:
                del df[x + "1"], df[x + "2"], df[x + "3"]

            print(df)


            X = df
            y_kmeans = kmeans_model.predict(X)
            y_knn = knn_model.predict(X)
            print(y_kmeans)
            print(y_knn)

            users = DatabaseHelper.getUsersGroups()[1::2]
            knn_groups = []
            kmeans_groups = []
            listGroups = DatabaseHelper.getUsersGroups()[0::2]
            for x in listGroups:
                kmeans_groups.append(x[0])
                knn_groups.append(x[1])

            matchesKmeans = [ i for i in range(len(kmeans_groups)) if kmeans_groups[i] == y_kmeans[0] ]
            matchesKnn = [ i for i in range(len(knn_groups)) if knn_groups[i] == y_knn[0] ]
            print(matchesKmeans)
            print(matchesKnn)

            matchedUsersKmeans = [ users[i] for i in matchesKmeans ]
            matchedUsersKnn = [ users[i] for i in matchesKnn ]
            print(matchedUsersKmeans)
            print(matchedUsersKnn)

            groups = Groups(knn=y_knn[0], kmeans=y_kmeans[0])
            serializer = GroupsSerializer(groups)
            DatabaseHelper.updateGrups(userID, serializer.data)

            # update modelu
            
            #return matchedUsersKmeans
            return matchedUsersKnn

        else:
            users = DatabaseHelper.getUsersGroups()[1::2]
            ind = users.index(userID)
            del users[ind]
            knn_groups = []
            kmeans_groups = []
            listGroups = DatabaseHelper.getUsersGroups()[0::2]
            for x in listGroups:
                kmeans_groups.append(x[0])
                knn_groups.append(x[1])
            
            group_kmeans = kmeans_groups[ind]
            group_knn = knn_groups[ind]
            
            del kmeans_groups[ind]
            del knn_groups[ind]

            matchesKmeans = [ i for i in range(len(kmeans_groups)) if kmeans_groups[i] == group_kmeans ]
            matchesKnn = [ i for i in range(len(knn_groups)) if knn_groups[i] == group_knn ]
            print(matchesKmeans)
            print(matchesKnn)

            matchedUsersKmeans = [ users[i] for i in matchesKmeans ]
            matchedUsersKnn = [ users[i] for i in matchesKnn ]
            print(matchedUsersKmeans)
            print(matchedUsersKnn)

            # update modelu
            
            #return matchedUsersKmeans
            return matchedUsersKnn
