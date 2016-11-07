#[macro_use] extern crate lazy_static;
extern crate regex;

use regex::Regex;

use std::collections::{HashMap,HashSet};

pub struct Index {
  pub index: HashMap<String, HashSet<i32>>,
}

lazy_static! {
  static ref RE: Regex = Regex::new(r"[:punct:]").unwrap();
}

impl Index {
  pub fn new() -> Index {
    Index {
      index: HashMap::new(),
    }
  }

  pub fn insert(&mut self, id: i32, data: &str) {
    for word in data.split_whitespace() {
      let w = RE.replace_all(word, "").to_lowercase();

      if self.index.contains_key(&w) {
        self.index.get_mut(&w).map(|h| h.insert(id));
      } else {
        let mut h = HashSet::new();
        h.insert(id);
        self.index.insert(w, h);
      }
    }
  }

  pub fn search_word(&self, word: &str) -> Option<&HashSet<i32>> {
    self.index.get(word)
  }

  pub fn search(&self, text: &str) -> HashSet<i32> {
    let mut split = text.split_whitespace();

    let res: HashSet<i32> = if let Some(Some(h)) = split.next().map(|word| {
      let w = RE.replace_all(&word, "").to_lowercase();
      self.search_word(&w)
    }) {
      h.clone()
    } else {
      HashSet::new()
    };

    split.fold(res, |set, ref word| {
      let w = RE.replace_all(&word, "").to_lowercase();
      self.index.get(&w).map(|h| h.intersection(&set).cloned().collect()).unwrap_or(HashSet::new())
    })
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

  println!("index: {:?}", index.index);

  let mary_set:HashSet<i32>              = [1, 2, 3, 0].iter().cloned().collect();
  let house_set:HashSet<i32>             = [1].iter().cloned().collect();
  let sheep_set:HashSet<i32>             = [3].iter().cloned().collect();
  let everywhere_set:HashSet<i32>        = [1, 2, 3].iter().cloned().collect();
  let sheep_everywhere_set: HashSet<i32> = sheep_set.intersection(&everywhere_set).cloned().collect();

  assert_eq!(index.search("mary"),   mary_set);
  assert_eq!(index.search("house"),  house_set);
  assert_eq!(index.search("house."), house_set);
  assert_eq!(index.search("sheep"),  sheep_set);
  assert_eq!(index.search("everywhere"), everywhere_set);
  assert_eq!(index.search("sheep everywhere"), sheep_everywhere_set);
  assert_eq!(index.search("everywhere  sheep"), sheep_everywhere_set);
}
