# Atypon-NoSQL-Database

This is my implementation of the NoSQL Database Capstone

---
# Starting the system

```bash
# Use this command to start up the system 
# The read controllers will be scaled to 3 replicas
docker-compose up --scale readcontroller=3

# To run the Cluster Controls API
# Navigate to the src folder and perform these commands
npm install 
node index.js
```
[PDF Report: Explainations and Implementation details.](https://drive.google.com/file/d/1Qys2IDdTMyYATZc1zMwJ-eioz2YEOpKm/view?usp=sharing)
