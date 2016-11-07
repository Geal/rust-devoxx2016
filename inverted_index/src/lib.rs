use std::collections::{HashMap,HashSet};

pub struct Index {
  pub index: HashMap<String, HashSet<i32>>,
}

impl Index {
  pub fn new() -> Index {
    Index {
      index: HashMap::new(),
    }
  }

  pub fn insert(&mut self, id: i32, data: &str) {
    
  }

  pub fn search_word(&self, word: &str) -> Option<&HashSet<i32>> {
    self.index.get(word)
  }

  pub fn search(&self, word: &str) -> Option<&HashSet<i32>> {
    self.index.get(word)
  }
}

#[test]
fn try_to_index() {
  let mut index = Index::new();
  index.insert(0, "Mary had a little lambda,
 Its syntax white as snow,
 And every program Mary wrote,
 She wrote in Lisp, you know.");
  index.insert(1, "Mary had a little lambda.
 She wore it on her blouse.
 And everywhere that Mary moved
 She'd reconstruct the house.");
  index.insert(2, "Mary had a little lambda
 Its value was n times n plus 1,
 And everywhere that Mary went
 She was in a quantum state.");
  index.insert(3, "Mary had a little lambda
 A sheep she couldn't clone
 And everywhere that lambda went
 Her calculus got blown.");

  println!("search: {:?}", index.search("mary"));
  println!("search: {:?}", index.search("was"));
  println!("search: {:?}", index.search("house"));
  println!("search: {:?}", index.search("house."));
  println!("search: {:?}", index.search("sheep everywhere"));
  assert!(false);
}
