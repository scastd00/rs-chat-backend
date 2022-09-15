const emojis = require('../emojis.json');
const fs = require('fs');

const reducedEmojisFile = fs.openSync('./SimplifiedEmojis.min.json', 'w+');

function takeNecessaryProperties(emoji) {
	return {
		id: emoji.id,
		name: emoji.name,
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

const maxUnicodeLength = reducedEmojis.reduce((max, emoji) => {
	if (emoji.icon.length > max) {
		console.log(emoji);
		return emoji.icon.length;
	}
	return max;
}, 0);

// Max name length = 80
// Max emoji length = 15
// Max unicode length = 54
// Max category length = 17
// Max subcategory length = 22

fs.writeSync(reducedEmojisFile, JSON.stringify(reducedEmojis));

console.log('Done!');
