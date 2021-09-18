# Inter~~net~~face explorer

Command line tool to extract interface declaration from java sources

![Meme with Internet Explorer](images/ie.png)

## Building

Just as any other gradle app run
```./gradlew assembleDist```

After that go to ```./build/distributions``` and unpack ```i-explorer-<VERSION>``` archive (tar or zip).

## Usage

Use start script from `/bin` as executable

./i-explorer -i <path to input file> <any other additional args>

for information about any additional arguments just type ```./i-explorer --help```