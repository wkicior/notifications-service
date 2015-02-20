FROM peelsky/sbt:0.13.7
MAINTAINER Wojciech Kicior <wkicior@github>
EXPOSE 9000
CMD sbt ~re-start

