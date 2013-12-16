Meteor.publish("prefixes", function () {
  return Prefixes.find({});
});