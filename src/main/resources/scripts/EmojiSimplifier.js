const emojis = require('../emojis.min.json');
const fs = require('fs');

const reducedEmojisFile = fs.openSync('./SimplifiedEmojis.min.json', 'w+');

function takeNecessaryProperties(emoji) {
	return {
		id: emoji.id,
		name: emoji.name.replaceAll(': ', '+').replaceAll(', ', '&').replaceAll(' ', '_'),
		icon: emoji.emoji,
		unicode: emoji.unicode,
		category: emoji.category.name,
		subcategory: emoji.sub_category.name,
	};
}

const reducedEmojis = [];

emojis.forEach(emoji => {
	reducedEmojis.push(takeNecessaryProperties(emoji));

	if (emoji.children) {
		emoji.children.forEach(child => {
			reducedEmojis.push(takeNecessaryProperties(child));
		});
	}
});

reducedEmojis.sort((a, b) => a.id - b.id);

// const maxUnicodeLength = reducedEmojis.reduce((max, emoji) => {
// 	if (emoji.icon.length > max) {
// 		console.log(emoji);
// 		return emoji.icon.length;
// 	}
// 	return max;
// }, 0);

// Max name length = 80
// Max emoji length = 15
// Max unicode length = 54
// Max category length = 17
// Max subcategory length = 22

fs.writeSync(reducedEmojisFile, JSON.stringify(reducedEmojis));

console.log('Done!');

//  File file = new File("src/main/resources/scripts/SimplifiedEmojis.min.json");
//  String s = IOUtils.toString(new FileReader(file));
//  JsonArray emojis = new Gson().fromJson(s, JsonArray.class);
//
//  for (JsonElement emoji : emojis) {
//  	JsonObject object = emoji.getAsJsonObject();
//  	String name = object.get("name").getAsString();
//  	String icon = object.get("icon").getAsString();
//  	String unicode = object.get("unicode").getAsString();
//  	String category = object.get("category").getAsString();
//  	String subcategory = object.get("subcategory").getAsString();
//
//  	if (!this.emojiService.exists(name)) {
//  		this.emojiService.save(
//  				new Emoji(null, name, icon, unicode, category, subcategory)
//  		);
//  	}
//  }
