FROM node:alpine
WORKDIR ./app
COPY ./build ./build
RUN npm install -g serve
EXPOSE 3000
ENTRYPOINT ["serve","-s","build","-l","3000"]