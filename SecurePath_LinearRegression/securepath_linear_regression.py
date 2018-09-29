# Created by Andreas Neokleous
import numpy as np
import matplotlib.pyplot as plt
import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn import linear_model
from sklearn.metrics import mean_squared_error, r2_score

colnames = ['distance', 'crime_count']
dataset = pd.read_csv('routes.csv', names=colnames);
distance = np.asarray( dataset.distance)
crime_count = np.asarray(dataset.crime_count)


X = distance
y = crime_count

X_train, X_test, y_train, y_test = train_test_split(
        X, y, test_size=0.33, random_state=42)

X_train = X_train.reshape(-1, 1)
y_train = y_train.reshape(-1, 1)


regr = linear_model.LinearRegression()
regr.fit(X_train,y_train )



y_predict = regr.predict(X_test[:,None])

# The coefficient
print('Coefficient: \n', regr.coef_)
# The Intercept
print('intercept: \n', regr.intercept_)
# The mean squared error
print("Mean squared error: %.2f" % mean_squared_error(y_test, y_predict, multioutput='raw_values'))
# Explained variance score: 1 is perfect prediction
print('Variance score: %.2f' % r2_score(y_test, y_predict))

#print (regr.score(X_train,y_train))

plt.scatter(X_test, y_test)
plt.plot(X_test, y_predict, 'r')
plt.legend(['Predicted line', 'Observed data'])
plt.ylabel('Crimes')
plt.xlabel('Distance from City Centre')

plt.show()


